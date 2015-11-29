package com.tryand.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tryand.codepathtodo.R;
import com.tryand.common.Utils;
import com.tryand.datastore.Note;
import com.tryand.datastore.NoteDataSource;

import java.util.List;

/**
 * Created by skammila on 11/14/15.
 */
public class NoteAdapter extends ArrayAdapter<Note> {
    NoteDataSource ndt;
    List<Note> noteList;
    public NoteAdapter(Context context, List<Note> notes, NoteDataSource dt) {
        super(context, 0, notes);
        this.noteList = notes;
        this.ndt = dt;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Note note = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.note_layout, parent, false);
        }
        // Lookup view for data population
        TextView noteText = (TextView) convertView.findViewById(R.id.noteText);
        TextView noteDate = (TextView) convertView.findViewById(R.id.noteDate);
        CheckBox noteStatus = (CheckBox) convertView.findViewById(R.id.noteStatus);
        // Populate the data into the template view using the data object

        noteText.setText(note.getNoteText());

        //set listview date color. If today, set it to red color
        noteDate.setText(note.getListViewDisplayDate());
        if (Utils.isDueToday(note.getDueDate())) {
            noteDate.setTextColor(Color.RED);
        } else {
            noteDate.setTextColor(Color.BLACK);
        }


        noteStatus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                Note note = (Note) cb.getTag();
                note.setStatus(cb.isChecked() ? 1 : 0);
                ndt.updateNote(note);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        NoteAdapter.this.reloadList();
                    }
                }, 200);
            }
        });

        noteStatus.setChecked(!note.isActive());
        noteStatus.setTag(note);
        return convertView;
    }

    public void reloadList() {
        this.noteList = ndt.getAllNotes();
        this.notifyDataSetChanged();
    }

    public int getCount() {
        return noteList.size();
    }

    public Note getItem(int position) {
        return noteList.get(position);
    }


}
