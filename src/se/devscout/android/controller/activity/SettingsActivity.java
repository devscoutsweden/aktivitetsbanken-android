package se.devscout.android.controller.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import se.devscout.android.R;

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
