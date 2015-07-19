package se.devscout.android.controller.activity;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.text.TextUtils;
import se.devscout.android.R;
import se.devscout.android.util.PreferencesUtil;

import java.util.Set;

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        ListPreference serverAddress = (ListPreference) findPreference("server_address");

        Set<String> serverAddresses = PreferencesUtil.getServerAddresses(this);
        String currentlySelectedHost = PreferencesUtil.getServerAddress(this);

        if (!TextUtils.isEmpty(currentlySelectedHost)) {
            serverAddresses.add(currentlySelectedHost);
        }
        String[] optionValues = new String[serverAddresses.size() + 1];
        String[] optionLabels = new String[serverAddresses.size() + 1];
        serverAddresses.toArray(optionValues);
        serverAddresses.toArray(optionLabels);
        optionValues[optionValues.length - 1] = "";
        optionLabels[optionLabels.length - 1] = "Default Server";

        serverAddress.setEntries(optionLabels);
        serverAddress.setEntryValues(optionValues);
        serverAddress.setEnabled(optionValues.length > 1);
    }
}
