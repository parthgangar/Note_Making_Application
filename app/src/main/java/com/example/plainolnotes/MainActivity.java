package com.example.plainolnotes;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EDITOR_REQUEST_CODE =100 ;
    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //insertNote("New Note");

        //Cursor cursor=getContentResolver().query(NotesProvider.CONTENT_URI,DBOpenHelper.ALL_COLUMNS,null,null,null);
        //the above is a cursor object that now has reference to all the data
        //for displaying data we will use a class called Simple cursor adapter

        String[] from ={DBOpenHelper.NOTE_TEXT};//this will be the list of columns that you want to display the data in your layout
        int[] to={R.id.tvNote};//list of resource id for views or controls that are going to be used for displaying
        //information; using a built-in resource id; that is the id of the text view that's going to be used in
        //a layout file along with the sdk

        //The loader interface that loads the data from the content provider asynchronously


        cursorAdapter = new NotesCursorAdapter(this,null,0);
        ListView list= (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,EditActivity.class);
                Uri uri=Uri.parse(NotesProvider.CONTENT_URI+"/"+id);
                intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE,uri);
                startActivityForResult(intent,EDITOR_REQUEST_CODE);
            }
        });


        getSupportLoaderManager().initLoader(0,null,this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {

            case R.id.snotes:insertSampleData();
                break;
            case R.id.dnotes:delNotes();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void delNotes() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            getContentResolver().delete(NotesProvider.CONTENT_URI,null,null);
                            restartLoader();
                            Toast.makeText(MainActivity.this,
                                    getString(R.string.all_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    private void insertSampleData() {

        insertNote("Sample Note 1");
        insertNote("Sample Note 2");
        restartLoader();
    }

    private void restartLoader() {
        getSupportLoaderManager().restartLoader(0,null,this);
    }

    private void insertNote(String noteText) {
        ContentValues values=new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT,noteText);   //here the key needs to be the name of the column you are assigning to
        Uri noteUri=getContentResolver().insert(NotesProvider.CONTENT_URI,values);
        Log.d("DB456","Inserted note "+noteUri.getLastPathSegment());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //this method is called when the data is needed from the content provider
        return new CursorLoader(this,NotesProvider.CONTENT_URI,null,null,null,null);

        //the second argument says that this is the uri where u can get the data
    }
    //the below two are event methods
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        cursorAdapter.swapCursor(data);
        // it executes the cursor object on the background thread

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
        //when the data needs to be wipe out
    }

    public void openEditorForNewNote(View view) {

        Intent intent=new Intent(MainActivity.this,EditActivity.class);
        startActivityForResult(intent,EDITOR_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==EDITOR_REQUEST_CODE && resultCode==RESULT_OK)
        {
            restartLoader();
        }
    }
}
