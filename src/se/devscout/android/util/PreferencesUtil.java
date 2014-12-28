package se.devscout.android.util;

import android.content.SharedPreferences;

import java.util.*;

public class PreferencesUtil {
    public static List<String> getStringList(SharedPreferences preferences, String prefKey, List<String> defaultOrder) {
        TreeMap<Integer, String> map = new TreeMap<Integer, String>();
        Set<String> prefValues = preferences.getStringSet(prefKey, null);
        if (prefValues != null) {
            for (String prefValue : prefValues) {
                int pos = prefValue.indexOf(',');
                String key = prefValue.substring(pos + 1);
                map.put(Integer.parseInt(prefValue.substring(0, pos)), key);
            }
            return new ArrayList<String>(map.values());
        } else {
            return defaultOrder;
        }
    }

    public static void putStringList(String prefKey, List<String> values, SharedPreferences.Editor preferencesEditor) {
        HashSet<String> prefValue = new HashSet<String>();
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            prefValue.add(i + "," + value);
        }
        preferencesEditor.putStringSet(prefKey, prefValue);
    }
}
