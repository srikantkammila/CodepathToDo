package com.tryand.codepathtodo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.tryand.datastore.Note;
import com.tryand.datastore.NoteDataSource;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

public class ItemEditActivity extends AppCompatActivity {
    EditText edt;
    RadioGroup statusRdadioGroup;
    RadioButton activeRd;
    RadioButton doneRd;
    Note nt;
    NoteDataSource datasource;
    String operation = "new";
    Calendar myCalendar = Calendar.getInstance();
    EditText dateInput;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);
        edt = (EditText) findViewById(R.id.editText);
        statusRdadioGroup = (RadioGroup) findViewById(R.id.note_status_radio);
        activeRd = (RadioButton) findViewById(R.id.status_active);
        doneRd = (RadioButton) findViewById(R.id.status_done);
        dateInput = (EditText) findViewById(R.id.date_input);
        dateInput.setFocusable(false);
        dateInput.setClickable(true);
        try {
            datasource = NoteDataSource.getOpenDatasource(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Bundle bd = getIntent().getBundleExtra("note_item");
        if (bd != null) {
            //Edit
            operation = "edit";
            setTitle("Edit");
            Note note = (Note) bd.get("note");
            this.nt = note;
            //set the form date
            edt.setText(nt.getNoteText());
            dateInput.setText(nt.getDisplayDate());
            if (nt.isActive()) {
                activeRd.setChecked(true);
                doneRd.setChecked(false);
            } else {
                activeRd.setChecked(false);
                doneRd.setChecked(true);
            }
        } else {
            //New
            setTitle("New");
            operation = "new";
            this.nt = new Note();
            //default status of new item
            activeRd.setChecked(true);
            doneRd.setChecked(false);
        }
        edt.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edt, InputMethodManager.SHOW_IMPLICIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_done:
                this.addUpdateItem();
                return true;
            case R.id.action_cancel:
                this.close();
                return true;
            case R.id.action_delete_item:
                this.delete();
                return true;

            default:
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.operation == "new") {
            //Hide delete button for New item view
            menu.getItem(2).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_edit, menu);
        this.menu =menu;
        return true;
    }


    public void addUpdateItem() {
        int checkedId = statusRdadioGroup.getCheckedRadioButtonId();
        int status = checkedId == R.id.status_active ? 0 : 1;
        String noteText = edt.getText().toString();
        if (operation == "edit") {
            // handle edit note
            this.nt.setNoteText(noteText);
            this.nt.setStatus(status);
            datasource.updateNote(nt);
        } else {
            //handle create note
            if (noteText != null && noteText.length() > 0) {
                datasource.createNote(noteText, status, nt.getDueDate(), "");
            }
        }
        this.finish();
    }

    public void close() {
        this.finish();
    }

    public void delete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Do you want to delete this note?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        datasource.deleteNote(nt);
                        close();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        datasource.close();
    }


    public void showDatepicker(View v) {

        Date dt = new Date();
        if (nt.getDueDate() > 0) {
            dt.setTime(nt.getDueDate());
        }
        myCalendar.setTime(dt);

        final View dateTimePicker = View.inflate(this, R.layout.date_time_picker_layout, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        dateTimePicker.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePicker datePicker = (DatePicker) dateTimePicker.findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker) dateTimePicker.findViewById(R.id.time_picker);

                myCalendar.set(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());

                long time = myCalendar.getTimeInMillis();
                updateDueDate(time);
                alertDialog.dismiss();
            }
        });
        ((DatePicker)dateTimePicker.findViewById(R.id.date_picker)).updateDate(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        ((TimePicker)dateTimePicker.findViewById(R.id.time_picker)).setCurrentHour(myCalendar.get(Calendar.HOUR_OF_DAY));
        ((TimePicker)dateTimePicker.findViewById(R.id.time_picker)).setCurrentMinute(myCalendar.get(Calendar.MINUTE));
        alertDialog.setView(dateTimePicker);
        alertDialog.show();
    }

    private void updateDueDate(long time) {
        nt.setDueDate(time);
        dateInput.setText(nt.getDisplayDate());

    }

}
