package com.tryand.datastore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by skammila on 11/11/15.
 */
public class NoteDataSource {
    private SQLiteDatabase database;
    private DoNoteHelper dbHelper;
    private String[] allColumns = { DoNoteHelper.COLUMN_ID,
            DoNoteHelper.COLUMN_NOTE, DoNoteHelper.COLUMN_NOTE_STATUS, DoNoteHelper.COLUMN_NOTE_DUE_DATE, DoNoteHelper.COLUMN_NOTE_EXTRA_NOTES, DoNoteHelper.COLUMN_NOTE_REMINDER, DoNoteHelper.COLUMN_NOTE_PRIORITY};

    public NoteDataSource(Context context) {
        dbHelper = new DoNoteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public static NoteDataSource getOpenDatasource(Context context) throws SQLException {
        NoteDataSource nds = new NoteDataSource(context);
        nds.open();
        return nds;
    }

    public Note createNote(String comment, int status, long time, String extraNotes, int reminderStatus, int priority) {
        ContentValues values = new ContentValues();
        values.put(DoNoteHelper.COLUMN_NOTE, comment);
        values.put(DoNoteHelper.COLUMN_NOTE_STATUS, status);
        values.put(DoNoteHelper.COLUMN_NOTE_DUE_DATE, time);
        values.put(DoNoteHelper.COLUMN_NOTE_EXTRA_NOTES, extraNotes);
        values.put(DoNoteHelper.COLUMN_NOTE_REMINDER, reminderStatus);
        values.put(DoNoteHelper.COLUMN_NOTE_PRIORITY, priority);

        long insertId = database.insert(DoNoteHelper.TABLE_NOTES, null,
                values);
        Cursor cursor = database.query(DoNoteHelper.TABLE_NOTES,
                allColumns, DoNoteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Note newComment = cursorToNote(cursor);
        cursor.close();
        return newComment;
    }

    public void updateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(DoNoteHelper.COLUMN_NOTE, note.getNoteText());
        values.put(DoNoteHelper.COLUMN_NOTE_STATUS, note.getStatus());
        values.put(DoNoteHelper.COLUMN_NOTE_DUE_DATE, note.getDueDate());
        values.put(DoNoteHelper.COLUMN_NOTE_REMINDER, note.getReminderStatus());
        values.put(DoNoteHelper.COLUMN_NOTE_PRIORITY, note.getPriority());

//        values.put(DoNoteHelper.COLUMN_NOTE_EXTRA_NOTES, extraNotes);

//        long insertId = database.insert(DoNoteHelper.TABLE_NOTES, null,
//                values);
        database.update(DoNoteHelper.TABLE_NOTES,
                values, DoNoteHelper.COLUMN_ID + " = " +note.getId(), null);
//        cursor.moveToFirst();
//        Note newComment = cursorToNote(cursor);
//        cursor.close();
//        return newComment;
    }

    public void deleteNote(Note note) {
        long id = note.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(DoNoteHelper.TABLE_NOTES, DoNoteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public void deleteDoneNotes() {
        System.out.println("Delete all the notes in 'Done' state");
        database.delete(DoNoteHelper.TABLE_NOTES, DoNoteHelper.COLUMN_NOTE_STATUS
                + " > " + "0", null);
    }

    public List<Note> getAllNotes() {
        List<Note> comments = new ArrayList<Note>();

        Cursor cursor = database.query(DoNoteHelper.TABLE_NOTES,
                allColumns, null, null, null, null, DoNoteHelper.COLUMN_NOTE_STATUS + " ASC, " + DoNoteHelper.COLUMN_NOTE_PRIORITY + " DESC, " + DoNoteHelper.COLUMN_NOTE_DUE_DATE + " ASC, " + DoNoteHelper.COLUMN_NOTE + " ASC" );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Note comment = cursorToNote(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }

    private Note cursorToNote(Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getLong(0));
        note.setNoteText(cursor.getString(1));
        note.setStatus(cursor.getInt(2));
        Date dt = new Date();
        String dtt = cursor.getString(3) != null && cursor.getString(3).length() > 0 ? cursor.getString(3) : "0";
//        dt.setTime();
        note.setDueDate(Long.parseLong(dtt));
        note.setExtraNotes(cursor.getString(4));
        note.setReminderStatus(cursor.getInt(5));
        note.setPriority(cursor.getInt(6));
        return note;
    }
}
