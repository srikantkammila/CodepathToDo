package com.tryand.codepathtodo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.tryand.adapter.NoteAdapter;
import com.tryand.datastore.Note;
import com.tryand.datastore.NoteDataSource;

import java.sql.SQLException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    NoteAdapter todoAdapter;
    ListView lvItems;
    EditText editText;
    NoteDataSource dataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            dataSource = NoteDataSource.getOpenDatasource(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pupulateItems();
        lvItems = (ListView)findViewById(R.id.todoItems);
        lvItems.setAdapter(todoAdapter);
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = todoAdapter.getItem(position);
                removeNote(note);
                todoAdapter.reloadList();
                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editItem = new Intent(MainActivity.this, ItemEditActivity.class);
                Bundle bd = new Bundle();
                bd.putSerializable("note", (Note) lvItems.getItemAtPosition(position));
                editItem.putExtra("note_item", bd);
                startActivity(editItem);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void removeNote(Note note) {
        dataSource.deleteNote(note);
    }

    private List<Note> readItemsFromDS() {
        return dataSource.getAllNotes();
    }

    public void pupulateItems () {
        todoAdapter = new NoteAdapter(this.getApplicationContext(), readItemsFromDS(), dataSource);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_add:
                Intent editItem = new Intent(MainActivity.this, ItemEditActivity.class);
                startActivity(editItem);
                return true;
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                    .setTitle("Delete")
                    .setMessage("Do you really want to delete all the selected notes?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            dataSource.deleteDoneNotes();
                            todoAdapter.reloadList();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
                return true;

            default:
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            dataSource.open();
            todoAdapter.notifyDataSetChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            dataSource.open();
            todoAdapter.reloadList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }
}
