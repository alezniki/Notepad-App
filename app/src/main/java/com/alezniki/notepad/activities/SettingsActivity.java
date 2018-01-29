package com.alezniki.notepad.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.alezniki.notepad.R;

/**
 * Settings activity screen
 * <p>
 * Created by nikola aleksic on 6/26/17.
 */
@SuppressWarnings("ALL")
public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Replace activity's content with an instance of PreferenceFragment
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new UserPreferences())
                .commit();
    }

    /**
     * User preferences
     * <p>
     * Automatically load preference GUI from an XML resource
     * and save preferences into preferences.xml file
     */
    public static class UserPreferences extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //Load Preference from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
