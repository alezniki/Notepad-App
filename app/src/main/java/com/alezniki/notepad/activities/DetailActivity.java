package com.alezniki.notepad.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alezniki.notepad.R;
import com.alezniki.notepad.model.DatabaseHelper;
import com.alezniki.notepad.model.Note;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

import static com.alezniki.notepad.activities.MainActivity.ALLOW_MESSAGE;
import static com.alezniki.notepad.adapter.NotesAdapter.KEY_ID;

/**
 * Details activity
 *
 * @author Nikola Aleksic
 */
public class DetailActivity extends AppCompatActivity {

    /**
     * Database helper
     */
    private DatabaseHelper helper = null;

    /**
     * Shared preferences
     */
    private SharedPreferences preferences;

    /**
     * Edited note title
     */
    private EditText etTitle;

    /**
     * Edited note text
     */
    private EditText etText;

    /**
     * Note
     */
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);

        //Receive the object that which has been sent through Intent from MainActivity
        int keyID = getIntent().getExtras().getInt(KEY_ID);

        try {
            note = getDatabaseHelper().getNotes().queryForId(keyID);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        etTitle = (EditText) findViewById(R.id.et_detail_note_title);
        etText = (EditText) findViewById(R.id.et_detail_note_text);

        etTitle.setText(note.getNoteTitle());
        etText.setText(note.getNoteText());

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Handle action bar item clicks here.
        int id = item.getItemId();

        switch (id) {

            case R.id.action_update_note: {
                updateNoteItem();
                break;
            }

            case R.id.action_delete_note: {
                deleteNoteItem();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Update note item
     */
    private void updateNoteItem() {

        note.setNoteTitle(etTitle.getText().toString());
        note.setNoteText(etText.getText().toString());

        try {
            getDatabaseHelper().getNotes().update(note);
            showNotificationMessage(getString(R.string.update_notification));
            finish();
        } catch (SQLException e) {
            e.printStackTrace();
            showNotificationMessage(getString(R.string.error_update_notification));
        }
    }

    /**
     * Delete note item
     */
    private void deleteNoteItem() {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(android.R.layout.select_dialog_item, null);

        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setMessage(getString(R.string.alert_message));

        //Set the view from XML inside AlertDialog
        alert.setView(alertLayout);

        //Disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);

        alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.dialog_negative_button),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dialog_positive_button),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (note != null) {
                            try {
                                getDatabaseHelper().getNotes().delete(note);
                                showNotificationMessage(getString(R.string.delete_notification));
                                finish();
                            } catch (SQLException e) {
                                e.printStackTrace();
                                showNotificationMessage(getString(R.string.error_delete_notification));
                            }
                        }
                        dialog.dismiss();
                    }
                });

        alert.show();
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
     * Get database helper
     *
     * @return database helper
     */
    private DatabaseHelper getDatabaseHelper() {

        if (helper == null) {
            helper = OpenHelperManager.getHelper(DetailActivity.this, DatabaseHelper.class);
        }

        return helper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (helper != null) {
            //Release database helper when done
            OpenHelperManager.releaseHelper();
            helper = null;
        }
    }
}
