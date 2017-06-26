package com.alezniki.notepad.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alezniki.notepad.R;
import com.alezniki.notepad.adapter.NotesAdapter;
import com.alezniki.notepad.model.DatabaseHelper;
import com.alezniki.notepad.model.Notes;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper helper = null;
    private SharedPreferences preferences;

    public static final int REQUEST_DATA_FROM_NOTES_ACTIVITY = 1;
    public static final String KEY_ID = "key_id";
    public static final String ALLOW_MSG = "allow_msg";

    private TextView tvTitle;
    private TextView tvText;

    private NotesAdapter adapter;
    private ListView listView;
    private List<Notes> list;

    private Notes note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);

        tvTitle = (TextView) findViewById(R.id.tv_note_title);
        tvText = (TextView) findViewById(R.id.tv_note_text);

        // Construct the data source
        list = new ArrayList<>();
        // Create the adapter to convert the array to views
        adapter = new NotesAdapter(this,list);
        // Attach the adapter to a ListView
        listView = (ListView) findViewById(R.id.lv_main_list_item);
        listView.setAdapter(adapter);

        try {
            list = getDatabaseHelper().getNotesDao().queryForAll();
            adapter.addAll(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NotesActivity.class);
                startActivityForResult(intent,REQUEST_DATA_FROM_NOTES_ACTIVITY);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // This holds the value of the Notes position , which user has selected for further action
                if (position > -1) {
                    Notes pos = (Notes) listView.getItemAtPosition(position);

                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra(KEY_ID, pos.getNoteID());

                    startActivity(intent);
                }
            }
        });

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DATA_FROM_NOTES_ACTIVITY) {
            
           if (resultCode == Activity.RESULT_OK) {
                // If user adds new note
                String title = data.getStringExtra("note_title");
                String text = data.getStringExtra("note_text");

                note = new Notes();
                note.setNoteTitle(title);
                note.setNoteText(text);

                // Create new note into database
                try {
                    getDatabaseHelper().getNotesDao().create(note);
                    showNotificationMessage(getString(R.string.create_notification));
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        listRefresh();
    }

    private void listRefresh() {
        if (listView != null && adapter != null) {
           // Clear the entire list
            adapter.clear();

            try {
                list = getDatabaseHelper().getNotesDao().queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            adapter.addAll(list); // Set up new elements
            adapter.notifyDataSetChanged(); // Refresh data
        }
    }

    private void showNotificationMessage(String message) {
        boolean allowed = preferences.getBoolean(ALLOW_MSG, false);
        if (allowed) {
            Snackbar.make(findViewById(R.id.lv_main_list_item), message, Snackbar.LENGTH_LONG).show();
        }
    }

    public DatabaseHelper getDatabaseHelper() {
        if (helper == null) {
            helper = OpenHelperManager.getHelper(MainActivity.this,DatabaseHelper.class);
        }

        return helper;
    }

    // Release Database Helper when done
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (helper != null) {
            OpenHelperManager.releaseHelper();
            helper = null;
        }
    }
}
