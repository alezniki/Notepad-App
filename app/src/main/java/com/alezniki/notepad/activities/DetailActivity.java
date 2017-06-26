package com.alezniki.notepad.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.alezniki.notepad.R;
import com.alezniki.notepad.model.DatabaseHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class DetailActivity extends AppCompatActivity {

    private DatabaseHelper helper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_update:
                Toast.makeText(this, "UPDATE", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_delete:
                Toast.makeText(this, "DELETE", Toast.LENGTH_SHORT).show();
                break;
        }


        return super.onOptionsItemSelected(item);
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
