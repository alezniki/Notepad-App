package com.alezniki.notepad.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

        // Handle the ACTION_SEARCH intent by checking for it in your onCreate() method.
        handleIntent(getIntent());

        Toast.makeText(this, "Main.onCreate", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_note).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setSubmitButtonEnabled(false);
//        searchView.setOnQueryTextListener(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform the final search, Triggered when the search is pressed
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Text has changed, apply filtering? Called when the user types each character in the text field

                adapter.getFilter().filter(newText);
                return true;
            }
        });

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
                    listRefresh();
                    showNotificationMessage(getString(R.string.create_notification));
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);

        Toast.makeText(this, "onNewIntent", Toast.LENGTH_SHORT).show();
//        listRefresh();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow

//            doMySearch(query);
        }
    }

    private void  doMySearch(String query) {

        try {
            getDatabaseHelper().getNotesDao().queryBuilder()
                    .where().eq(Notes.FIELD_NAME_TITLE, query)
                    .or().eq(Notes.FIELD_NAME_TEXT,query)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        listRefresh();

//        handleIntent(getIntent());

        Toast.makeText(this, "Main.onResume", Toast.LENGTH_SHORT).show();
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

//            handleIntent(getIntent());
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
