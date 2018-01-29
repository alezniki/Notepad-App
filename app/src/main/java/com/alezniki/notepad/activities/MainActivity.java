package com.alezniki.notepad.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alezniki.notepad.R;
import com.alezniki.notepad.adapter.NotesAdapter;
import com.alezniki.notepad.model.DatabaseHelper;
import com.alezniki.notepad.model.Note;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main activity
 *
 * @author Nikola Aleksic
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Database helper
     */
    private DatabaseHelper helper = null;

    /**
     * Shared preferences
     */
    private SharedPreferences preferences;

    /**
     * Request data from notes activity
     */
    private static final int REQUEST_DATA_FROM_NOTES_ACTIVITY = 1;

    /**
     * Allow message
     */
    public static final String ALLOW_MESSAGE = "allow_msg";

    /**
     * Display grid
     */
    public static final String DISPLAY_GREED = "display_grid";

    /**
     * Notes adapter
     */
    private NotesAdapter adapter;

    /**
     * List of notes
     */
    private List<Note> notes;

    /**
     * Recycler view
     */
    private RecyclerView recyclerView;

    /**
     * Layout manager
     */
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);

        //Construct the data source
        notes = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        //1.Use this setting to improve performance if you know that changes
        //in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        //2.Use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //3.Create the adapter to convert the array to views
        adapter = new NotesAdapter(this, notes);
        recyclerView.setAdapter(adapter);

        try {
            notes = getDatabaseHelper().getNotes().queryForAll();
            adapter.addToAdapter(notes);
            adapter.notifyDataSetChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Add note button and handle click event
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.add_note_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NotesActivity.class);
                startActivityForResult(intent, REQUEST_DATA_FROM_NOTES_ACTIVITY);
            }
        });

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        getGridView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search_note);

        //Get the SearchView and set the searchable configuration
        SearchView searchView = (SearchView) item.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        //Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //Do not iconify the widget; expand it by default
        searchView.setIconifiedByDefault(false);

        searchView.setSubmitButtonEnabled(false);
        //searchView.setOnQueryTextListener(this);

        searchQuery(searchView);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Handle action bar item clicks here.
        int id = item.getItemId();

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

                //If user adds new note
                String title = data.getStringExtra("note_title");
                String text = data.getStringExtra("note_text");

                Note note = new Note();
                note.setNoteTitle(title);
                note.setNoteText(text);

                try {
                    //Create new note into database
                    getDatabaseHelper().getNotes().create(note);
                    refresh();
                    showNotificationMessage(getString(R.string.create_notification));
                } catch (SQLException e) {
                    e.printStackTrace();
                    showNotificationMessage(getString(R.string.error_create_notification));
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter = new NotesAdapter(this, notes);
        recyclerView.setAdapter(adapter);

        refresh();
        getGridView();
    }


    /**
     * Refresh list
     * <p>
     * Updates list of notes
     */
    private void refresh() {

        if (recyclerView != null && adapter != null) {

            //Clear the entire list
            adapter.clearFromAdapter();

            try {
                notes = getDatabaseHelper().getNotes().queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Set up new elements and refresh data
            adapter.addToAdapter(notes);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Search query
     * <p>
     * Implement OnQueryTextListener
     *
     * @param searchView search view
     */
    private void searchQuery(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Perform the final search, triggered when the search is pressed
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Text has changed, called when the user types each character in the text field
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    /**
     * Show notification message
     *
     * @param message message
     */
    private void showNotificationMessage(String message) {

        boolean isAllowed = preferences.getBoolean(ALLOW_MESSAGE, false);

        if (isAllowed) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get grid view
     */
    private void getGridView() {

        recyclerView.setLayoutManager(layoutManager);

        boolean hasGrid = preferences.getBoolean(DISPLAY_GREED, false);

        if (hasGrid) {
            //Create display layout with two columns
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
    }

    /**
     * Get database helper
     *
     * @return database helper
     */
    private DatabaseHelper getDatabaseHelper() {

        if (helper == null) {
            helper = OpenHelperManager.getHelper(MainActivity.this, DatabaseHelper.class);
        }

        return helper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Release Database Helper when done
        if (helper != null) {
            OpenHelperManager.releaseHelper();
            helper = null;
        }
    }
}
