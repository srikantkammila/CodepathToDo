package com.tryand.datastore;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by skammila on 11/10/15.
 */
public class DoNoteHelper extends SQLiteOpenHelper {

    public static final String TABLE_NOTES = "NOTES";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_NOTE_DUE_DATE = "due_date";
    public static final String COLUMN_NOTE_STATUS = "status"; //true -> activ, false -> done
    public static final String COLUMN_NOTE_EXTRA_NOTES = "extra_notes";



    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 5;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NOTES + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_NOTE
            + " text not null, " + COLUMN_NOTE_STATUS
            + " integer, " + COLUMN_NOTE_DUE_DATE
            + " text, " + COLUMN_NOTE_EXTRA_NOTES
            + " text);";
    
    public DoNoteHelper(Context cxt) {
        super(cxt, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DoNoteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }
}
