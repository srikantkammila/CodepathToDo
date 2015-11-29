package com.tryand.datastore;

import com.tryand.common.Utils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by skammila on 11/11/15.
 */
public class Note implements Serializable {
    private long id;
    private String noteText;
    private long dueDate;
    private int status = 0; //0 -> Active, 1 -> done
    private String extraNotes;

    public Note() {}

    public Note(String text, long id) {
        this.noteText = text;
        this.id = id;
    }

    public Note(String text, int status, String extraNotes, long dueDate) {
        this.noteText = text;
        this.status = status;
        this.extraNotes = extraNotes;
        this.dueDate = dueDate;
    }

    public long getId() {
        return id;
    }

    public String getNoteText() {
        return noteText;
    }

    public long getDueDate() {
        return dueDate;
    }

    public String getDisplayDate() {
        SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yyyy   hh:mm a");
        String displayDate = dueDate > 0 ? ft.format(new Date(dueDate)) : "";
        return displayDate;
    }

    public String getListViewDisplayDate() {
        SimpleDateFormat ft = null;
        String displayDate = "";
        if (dueDate > 0 && Utils.isDueToday(dueDate)) {
            ft = new SimpleDateFormat("hh:mm a");
            displayDate = ft.format(new Date(dueDate));
        } else if (dueDate > 0) {
            ft = new SimpleDateFormat("MM/dd/yyyy");
            displayDate = ft.format(new Date(dueDate));
        }
//        SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yyyy   hh:mm a");
//        String displayDate = dueDate != 0 ? ft.format(new Date(dueDate)) : "";
        return displayDate;
    }

    public boolean isActive() {
        return status == 0;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public String getExtraNotes() {
        return extraNotes;
    }

    public void setExtraNotes(String extraNotes) {
        this.extraNotes = extraNotes;
    }

    @Override
    public String toString() {
        return this.noteText;
    }
}
