package com.alezniki.notepad.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alezniki.notepad.R;
import com.alezniki.notepad.model.DatabaseHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper helper = null;
    public final int REQUEST_DATA_FROM_NOTES_ACTIVITY = 1;


    private TextView tvTitle;
    private TextView tvText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);

        tvTitle = (TextView) findViewById(R.id.tv_note_title);
        tvText = (TextView) findViewById(R.id.tv_note_text);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,NotesActivity.class));
            }
        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DATA_FROM_NOTES_ACTIVITY) {

            if (resultCode == Activity.RESULT_CANCELED){
                // If there is no input result from the user
                tvTitle.getText();
                tvText.getText();

            } else if (resultCode == Activity.RESULT_OK){
                // If user adds new note
                String title = data.getStringExtra("note_title");
                String text = data.getStringExtra("note_text");

            }
        }
    }

    public DatabaseHelper getDatabaseHelper() {
        if (helper == null) {
            helper = OpenHelperManager.getHelper(this,DatabaseHelper.class);
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
