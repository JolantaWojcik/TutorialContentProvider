package com.example.jola.tutorialcontentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Jola on 9/28/2015.
 */
public class NamesProvider extends ContentProvider {

    public static final String NAME = "name";
    public static final String _ID = "_id";

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "AddedNames";
    static final String NAMES_TABLE = "tableOfNames";
    static final int DATABASE_VERSION = 1;
    private SQLDatabase sqlDatabase;

    static final String AUTHORITY = "com.example.jola.tutorialcontentprovider.NamesProvider";
    static final String URL = "content://" + AUTHORITY + "/" + NAMES_TABLE;
    public static final Uri CONTENT_URI = Uri.parse(URL);

    //Utility class to aid in matching URIs in content providers.
    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    //To use this class, build up a tree of UriMatcher
    public static final int NAME_NUM = 1;
    public static final int NAME_ID = 2;

    static {
        //public void addURI(String authority, String path, int code)
       //Add a URI to match, and the code to return when this URI is matched.
        uriMatcher.addURI(AUTHORITY, NAMES_TABLE, NAME_NUM);
        uriMatcher.addURI(AUTHORITY, NAMES_TABLE + "/#", NAME_ID);
    }

    @Override
    public boolean onCreate() {
        sqlDatabase = new SQLDatabase(getContext(), null, null, 1);
        db = sqlDatabase.getWritableDatabase();
        if (db != null) {
            return true;
        }
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Implement this to handle query requests from clients.

        // SQLiteQueryBuilder is a helper class that creates the
        // proper SQL syntax for us.
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        // Set the table we're querying.
        queryBuilder.setTables(NAMES_TABLE);

        int uriType = uriMatcher.match(uri);

        //public void appendWhere(CharSequence inWhere)
       // Append a chunk to the WHERE clause of the query.
        switch (uriType) {
            case NAME_ID:
                queryBuilder.appendWhere(_ID + "=" + uri.getLastPathSegment());
                break;
            case NAME_NUM:

                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
        // Make the query.
        Cursor cursor = queryBuilder.query(sqlDatabase.getWritableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        /*
        Implement this to handle requests for the MIME type of the data at the given URI.
        The returned MIME type should start with
        vnd.android.cursor.item for a single record,
        or vnd.android.cursor.dir/ for multiple items.
        This method can be called from multiple threads, as described in Processes and Threads.
         */
        String contentType = "vnd.android.cursor.item/vnd.example.names";

        return contentType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
        Implement this to handle requests to insert a new row. As a courtesy, call notifyChange() after inserting.
        This method can be called from multiple threads, as described in Processes and Threads.
         */
        db = sqlDatabase.getWritableDatabase();
        long rowID = db.insert(NAMES_TABLE, null, values);

        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db = sqlDatabase.getWritableDatabase();
        int uriType = uriMatcher.match(uri);
        int rowsDeleted = 0;

        switch (uriType) {
            case NAME_NUM:
                //delete all ->
                rowsDeleted = db.delete(NAMES_TABLE, selection, selectionArgs);

              //  rowsDeleted = db.delete(NAMES_TABLE, NAME + " = ? ", new String[]{"alaa"});
                break;
            case NAME_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(NAMES_TABLE, _ID + "=" + id, null);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        /*
        Implement this to handle requests to update one or more rows. The implementation should update all rows matching the selection
        to set the columns according to the provided values map.
        As a courtesy, call notifyChange() after updating. This method can be called from multiple threads, as described in Processes and Threads.
         */
        int count =0;
        int uriType = uriMatcher.match(uri);
        db = sqlDatabase.getWritableDatabase();

        switch (uriType) {
            case NAME_NUM:
                count = db.update(NAMES_TABLE, values, selection, selectionArgs);
                break;
            case NAME_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    count = db.update(NAMES_TABLE,values,_ID + "=" + id, null);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    private class SQLDatabase extends SQLiteOpenHelper{

        public SQLDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = " CREATE TABLE " + NAMES_TABLE +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " name TEXT NOT NULL);";
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + NAMES_TABLE);
            onCreate(db);
        }
    }

}
