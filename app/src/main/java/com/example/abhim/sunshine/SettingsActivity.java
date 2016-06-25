package com.example.abhim.sunshine;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by abhim on 6/23/2016.
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Add 'general' preferences, defined in the xml file
        addPreferencesFromResource(R.xml.pref_general);

        //For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preferences change
        bindPReferencesSummaryToValue(findPreference(getString(R.string.pref_location_key)));
        bindPReferencesSummaryToValue(findPreference(getString(R.string.pref_units_key)));

    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed).
     *
     * @param preference
     */
    private void bindPReferencesSummaryToValue(Preference preference) {

        //Set the listener to watch the values changes.
        preference.setOnPreferenceChangeListener(this);

        //Trigger the listener immediately with the preference's
        //current value.
        onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        String stringValue = newValue.toString();

        if (preference instanceof ListPreference) {
            //For list preferences, look up the correct display value in
            //the preferences's 'entries' list (since they have separate labels/values
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(stringValue);
            } else {
                // For other preferences, set the summary to the value's simple string representation.
                preference.setSummary(stringValue);
            }
        }
        return true;
    }
}
