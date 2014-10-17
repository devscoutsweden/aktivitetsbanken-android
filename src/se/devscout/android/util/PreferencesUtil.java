package se.devscout.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import se.devscout.android.model.ObjectIdentifierBean;
import se.devscout.server.api.ActivityBank;
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

    public synchronized UserKey getCurrentUser() {
        if (mPreferences.contains("current_user_id")) {
            long currentUserId = mPreferences.getLong("current_user_id", 0);
            LogUtil.d(PreferencesUtil.class.getName(), "Getting current_user_id = " + currentUserId);
            return new ObjectIdentifierBean(currentUserId);
        } else {
            /*
             * This happens when the app is started for the first time.
             *
             * The database has not yet been created and the current_user_id has
             * not been set. Return the value which we know that the first
             * created user will have, namely the first primary value generated
             * for any SQLite table (=1).
             */
            Long defaultUserId = ActivityBank.DEFAULT_USER_ID;
            LogUtil.d(PreferencesUtil.class.getName(), "Getting current_user_id = " + defaultUserId + " (fallback)");
            return new ObjectIdentifierBean(defaultUserId);
        }
    }

    public synchronized void setCurrentUser(UserKey userKey) {
        boolean commit = mPreferences.edit().putLong("current_user_id", userKey.getId()).commit();
        Log.i(PreferencesUtil.class.getName(), "Setting current_user_id = " + userKey.getId() + " (success: " + commit + ")");
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
