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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
    public static final String ALLOW_MSG = "allow_msg";
    public static final String DISPLAY_GREED = "display_grid";


    private NotesAdapter adapter;
    private List<Notes> list;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private Notes note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);

        // Construct the data source
        list = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        // 1. Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // 2. Use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //3. Create the adapter to convert the array to views
        adapter = new NotesAdapter(this, list);
        recyclerView.setAdapter(adapter);

        try {
            list = getDatabaseHelper().getNotesDao().queryForAll();
            adapter.addToAdapter(list);
            adapter.notifyDataSetChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
//        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_note).getActionView();
        MenuItem item = menu.findItem(R.id.action_search_note);
        SearchView searchView = (SearchView) item.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setSubmitButtonEnabled(false);
//        searchView.setOnQueryTextListener(this);

        searchQuery(searchView);

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
    protected void onResume() {
        super.onResume();

        adapter = new NotesAdapter(this,list);
        recyclerView.setAdapter(adapter);

        listRefresh();
        getGridView();

//        Toast.makeText(this, "Main.onResume", Toast.LENGTH_SHORT).show();
    }

    private void listRefresh() {
        if (recyclerView != null && adapter != null) {
           // Clear the entire list
            adapter.clearFromAdapter();

            try {
                list = getDatabaseHelper().getNotesDao().queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            adapter.addToAdapter(list); // Set up new elements
            adapter.notifyDataSetChanged(); // Refresh data

        }
    }

    //  implement OnQueryTextListener
    private void searchQuery(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform the final search, Triggered when the search is pressed
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Text has changed, Called when the user types each character in the text field
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }


    private void showNotificationMessage(String message) {
        boolean allowed = preferences.getBoolean(ALLOW_MSG, false);

        if (allowed) {
            Snackbar.make(findViewById(R.id.recycler), message, Snackbar.LENGTH_LONG).show();
        }
    }

    private void getGridView() {
        recyclerView.setLayoutManager(layoutManager);

        boolean grid = preferences.getBoolean(DISPLAY_GREED, false);

        // Create display layout with 2 columns
        if (grid) recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

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
