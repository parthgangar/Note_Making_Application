package com.example.plainolnotes;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Parth Gangar on 11-07-2017.
 */
//the class CursorAdapter is the superclass of the class SimpleCursorAdapter
//SCA knows how to directly pass the text from the database into the layout
//if we want to do it dynamically then we will have to use Custom Cursor Adapter
public class NotesCursorAdapter extends CursorAdapter {


    public NotesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.note_list_item,parent,false);
        //so i am reading and inflating for the note list item and passing it back whenever the method is called
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String noteText=cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
        int pos=noteText.indexOf(10);//10 is ascii value of line feed character
        if(pos!=-1)
        {
            noteText=noteText.substring(0,pos)+"...";
        }
        TextView tv= (TextView) view.findViewById(R.id.tvNote);
        tv.setText(noteText);
    }
}
