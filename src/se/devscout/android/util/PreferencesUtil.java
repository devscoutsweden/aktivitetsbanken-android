package se.devscout.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import se.devscout.android.model.ObjectIdentifierBean;
import se.devscout.server.api.model.UserKey;

import java.util.*;

public class PreferencesUtil {
    private static PreferencesUtil instance;
    private Context mContext;
    private SharedPreferences mPreferences;

    private PreferencesUtil(Context context) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public static PreferencesUtil getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesUtil(context);
        }
        return instance;
    }

    public UserKey getCurrentUser() {
        if (mPreferences.contains("current_user_id")) {
            return new ObjectIdentifierBean(mPreferences.getLong("current_user_id", 0));
        } else {
            return null;
        }
    }

    public void setCurrentUser(UserKey userKey) {
        mPreferences.edit().putLong("current_user_id", userKey.getId()).commit();
    }

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
