package com.example.jola.tutorialcontentprovider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private TextView textView;
    private String name;
    ContentResolver contentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.textView);

        //This class provides applications access to the content model.
        contentResolver = getContentResolver();
    }

    public void addRecord(View view) {
        name = editText.getText().toString();

        //This class is used to store a set of values that the ContentResolver can process.
        //ContentValues() -> Creates an empty set of values using the default initial size
        ContentValues contentValues = new ContentValues();
        //put(String key, String value)
        contentValues.put(NamesProvider.NAME, name);
        //insert(Uri, ContentValues) which inserts new data into the content provider
        Uri uri = contentResolver.insert(NamesProvider.CONTENT_URI, contentValues);
    }

    public void deleteRecord(View view) {
        name = editText.getText().toString();
        //delete(Uri uri, String selection, String[] selectionArgs)
        contentResolver.delete(NamesProvider.CONTENT_URI, null, null);
    }

    public void showRecords(View view) {
       //query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
       // Query the given URI, returning a Cursor over the result set.
        Cursor cursor = contentResolver.query(NamesProvider.CONTENT_URI, null, null, null, null);
        cursor.moveToFirst();

        StringBuilder stringBuilder = new StringBuilder();
        while (!cursor.isAfterLast()) {
            //StringBuilder	append(char c)
           // Appends the string representation of the specified char value.
            stringBuilder.append("\n" + cursor.getString(cursor.getColumnIndex("_id")) + " " + cursor.getString(cursor.getColumnIndex("name")));
            cursor.moveToNext();
        }
        textView.setText(stringBuilder);
    }
}
