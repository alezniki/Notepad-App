package com.alezniki.notepad.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.alezniki.notepad.R;

/**
 * Note activity
 *
 * @author Nikola Aleksic
 */
public class NotesActivity extends AppCompatActivity {

    /**
     * Edited note title
     */
    private EditText etTitle;

    /**
     * Edited note text
     */
    private EditText etText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);

        etTitle = (EditText) findViewById(R.id.et_save_note_title);
        etText = (EditText) findViewById(R.id.et_save_note_text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_save_note) {
            saveNote();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Save note
     * <p>
     * Send data about created note back to main activity
     */
    private void saveNote() {

        String title = etTitle.getText().toString();
        String text = etText.getText().toString();

        if (title.trim().isEmpty() && text.trim().isEmpty()) {

            //If there is no input from the user return to main activity
            finish();
        } else {

            // Send note data
            Intent intent = new Intent();

            intent.putExtra("note_title", title);
            intent.putExtra("note_text", text);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}
