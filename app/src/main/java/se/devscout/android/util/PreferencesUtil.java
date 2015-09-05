package se.devscout.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class PreferencesUtil {

    private static final String DEFAULT_ERROR_REPORTING_MAIL_ADDRESS = "devscout@mikaelsvensson.info";
    private static final String PREF_CONTACT_ERROR = "contact_error";
    private static final String PREF_DISMISSED_MESSAGES = "dismissed_messages";
    private static final String PREF_SERVER_ADDRESSES = "server_addresses";

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

    public static String getErrorReportMailAddress(Context context) {
        return getSharedPrefs(context).getString(PREF_CONTACT_ERROR, DEFAULT_ERROR_REPORTING_MAIL_ADDRESS);
    }

    private static SharedPreferences getSharedPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setErrorReportMailAddress(Context context, String address) {
        if (address != null) {
            getSharedPrefs(context).edit().putString(PREF_CONTACT_ERROR, address).commit();
        } else {
            getSharedPrefs(context).edit().remove(PREF_CONTACT_ERROR).commit();
        }
    }

    public static Set<String> getServerAddresses(Context context) {
        return new TreeSet<>(getSharedPrefs(context).getStringSet(PREF_SERVER_ADDRESSES, Collections.<String>emptySet()));
    }

    public static void setServerAddresses(Context context, HashSet<String> hosts) {
        getSharedPrefs(context).edit().putStringSet(PREF_SERVER_ADDRESSES, hosts).commit();
    }

    public static String getServerAddress(Context context) {
        return getSharedPrefs(context).getString("server_address", "");
    }

    public static boolean isMessageDismissed(Context context, String id) {
        final Set<String> dismissedMessages = getSharedPrefs(context).getStringSet(PREF_DISMISSED_MESSAGES, Collections.<String>emptySet());
        return dismissedMessages.contains(id);
    }

    public static void setMessageDismissed(Context context, String id) {
        final Set<String> dismissedMessages = getSharedPrefs(context).getStringSet(PREF_DISMISSED_MESSAGES, new HashSet<String>());
        dismissedMessages.add(id);
        getSharedPrefs(context).edit().putStringSet(PREF_DISMISSED_MESSAGES, dismissedMessages).commit();
    }
    public static void resetAllDismissedMessages(Context context) {
        getSharedPrefs(context).edit().putStringSet(PREF_DISMISSED_MESSAGES, new HashSet<String>()).commit();
    }
}
