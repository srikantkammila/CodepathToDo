package com.tryand.codepathtodo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tryand.R;
import com.tryand.datastore.Note;
import com.tryand.datastore.NoteDataSource;
import com.tryand.notification.RemindItemDueReceiver;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by skammila on 12/14/15.
 */
public class ItemEditDialogFragment extends DialogFragment {

    private EditText mEditText;

    EditText edt;
    RadioGroup statusRdadioGroup;
    RadioButton activeRd;
    RadioButton doneRd;
    RadioGroup reminderRadioGroup;
    RadioButton reminderActive;
    RadioButton reminderInactive;
    Spinner prioritySpinner;
    String[] priorityValues = {"Low", "Medium", "High"};
    Note nt;
    NoteDataSource datasource;
    String operation = "new";
    Calendar myCalendar = Calendar.getInstance();
    EditText dateInput;
    Menu menu;
    RemindItemDueReceiver reminderRsv = new RemindItemDueReceiver();
    ImageButton dateSelect;

    public ItemEditDialogFragment() {
    }

    public static ItemEditDialogFragment newInstance() {
        ItemEditDialogFragment frag = new ItemEditDialogFragment();
//        Bundle args = new Bundle();
//        args.putString("title", title);
//        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_edit_fragment, container);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mEditText = (EditText) view.findViewById(R.id.editText);
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }



    public void addUpdateItem() {
        int checkedId = statusRdadioGroup.getCheckedRadioButtonId();
        int status = checkedId == R.id.status_active ? 0 : 1;
        int reinderCheckedId = reminderRadioGroup.getCheckedRadioButtonId();
        int reminderStatus = reinderCheckedId == R.id.reminder_status_active ? 0 : 1;
        String noteText = edt.getText().toString();
        if (operation == "edit") {
            // handle edit note
            this.nt.setNoteText(noteText);
            this.nt.setStatus(status);
            this.nt.setReminderStatus(reminderStatus);
            datasource.updateNote(nt);
        } else {
            //handle create note
            if (noteText != null && noteText.length() > 0) {
                this.nt = datasource.createNote(noteText, status, nt.getDueDate(), "", reminderStatus, nt.getPriority());
            }
        }
        if (this.nt.isReminderActive() && this.nt.getDueDate() > 0) {
            Logger.getLogger("ToDo").info("Reminder for " + this.nt.getDisplayDate());
            reminderRsv.setAlarm(getActivity(), (int)this.nt.getId(), this.nt.getNoteText(), this.nt.getDueDate() - 15 * 60 * 1000);
        } else {
            reminderRsv.cancelAlarm(getActivity(), (int) this.nt.getId());
        }
        this.close();
    }

    public void close() {
        this.dismiss();
        ((MainActivity)getActivity()).todoAdapter.reloadList();
    }

    public void delete() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete")
                .setMessage("Do you want to delete this note?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (nt.getDueDate() > 0 && nt.isReminderActive()) {
                            reminderRsv.cancelAlarm(getActivity(), (int) nt.getId());
                        }
                        datasource.deleteNote(nt);
                        close();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    public void initView(View view) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // set the listener for Navigation
        Toolbar actionBar = (Toolbar) view.findViewById(R.id.fake_action_bar);
        actionBar.inflateMenu(R.menu.menu_item_edit);
//        actionBar.setTitle("Edit");
        if (actionBar!=null) {
            final ItemEditDialogFragment window = this;
            actionBar.setNavigationOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    window.dismiss();
                }
            });
            actionBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {

                        case R.id.action_done:
                            ItemEditDialogFragment.this.addUpdateItem();
                            return true;
                        case R.id.action_cancel:
                            ItemEditDialogFragment.this.close();
                            return true;
                        case R.id.action_delete_item:
                            ItemEditDialogFragment.this.delete();
                            return true;

                        default:
                            // Invoke the superclass to handle it.
                            return false;

                    }
                }
            });
        }
        edt = (EditText) view.findViewById(R.id.editText);
        statusRdadioGroup = (RadioGroup) view.findViewById(R.id.note_status_radio);
        activeRd = (RadioButton) view.findViewById(R.id.status_active);
        doneRd = (RadioButton) view.findViewById(R.id.status_done);
        reminderRadioGroup = (RadioGroup) view.findViewById(R.id.note_reminder_status_radio);
        reminderActive = (RadioButton) view.findViewById(R.id.reminder_status_active);
        reminderInactive = (RadioButton) view.findViewById(R.id.reminder_status_inactive);
        dateInput = (EditText) view.findViewById(R.id.date_input);
        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemEditDialogFragment.this.showDatepicker(v);
            }
        });
        dateSelect = (ImageButton) view.findViewById(R.id.date_select);
        dateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemEditDialogFragment.this.showDatepicker(v);
            }
        });
        prioritySpinner = (Spinner) view.findViewById(R.id.note_priority_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, priorityValues);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);
        prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nt.setPriority(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //default low priority
            }
        });
        reminderActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(nt.getDueDate() > 0)) {
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getActivity(), "Set a Due Date/Time", duration);
                    toast.show();
                    reminderActive.setChecked(false);
                    reminderInactive.setChecked(true);
                }
            }
        });
        dateInput.setFocusable(false);
        dateInput.setClickable(true);
        datasource = ((MainActivity)getActivity()).dataSource;
//        Bundle bd = getgetIntent().getBundleExtra("note_item");
        Note note = getArguments() != null ? (Note)(getArguments().get("note")) : null;

        if (note != null) {
            //Edit
            operation = "edit";
            actionBar.setTitle("Edit");

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
            if (nt.isReminderActive()) {
                reminderActive.setChecked(true);
                reminderInactive.setChecked(false);
            } else {
                reminderInactive.setChecked(true);
                reminderActive.setChecked(false);
            }
            prioritySpinner.setSelection(nt.getPriority());
        } else {
            //New
            actionBar.setTitle("New");
            operation = "new";
            this.nt = new Note();
            //default status of new item
            activeRd.setChecked(true);
            doneRd.setChecked(false);

            reminderActive.setChecked(false);
            reminderInactive.setChecked(true);
            prioritySpinner.setSelection(0);

            actionBar.getMenu().getItem(2).setVisible(false);
        }
        edt.requestFocus();
    }

    public void showDatepicker(View v) {

        Date dt = new Date();
        if (nt.getDueDate() > 0) {
            dt.setTime(nt.getDueDate());
        }
        myCalendar.setTime(dt);

        final View dateTimePicker = View.inflate(getActivity(), R.layout.date_time_picker_layout, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        dateTimePicker.findViewById(R.id.date_time_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //reset reminder to inactive
                reminderInactive.setChecked(true);
                reminderActive.setChecked(false);

                updateDueDate(0);
                alertDialog.dismiss();
            }
        });

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