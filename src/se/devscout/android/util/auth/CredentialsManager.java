package se.devscout.android.util.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.android.model.ObjectIdentifierBean;
import se.devscout.android.model.UserKey;
import se.devscout.android.model.UserProperties;
import se.devscout.android.model.repo.remote.ServerAPICredentials;
import se.devscout.android.util.IdentityProvider;
import se.devscout.android.util.LogUtil;
import se.devscout.server.api.ActivityBank;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton for keeping track of user authentication, meaning that it is in
 * charge of knowing whether or not a user is signed in. It also "manages" the
 * authentication process using an AuthenticationStrategy.
 */
public class CredentialsManager {

    private static final String CURRENT_USER_ID = "current_user_id";
    private static CredentialsManager instance = null;
    private final SharedPreferences mPreferences;
    private State mState = State.LOGGED_OUT;
    private IdentityProvider mIdentityProvider;
    private ServerAPICredentials mServerAPICredentials;

    public ServerAPICredentials getServerAPICredentials() {
        return mServerAPICredentials;
    }

    public IdentityProvider getIdentityProvider() {
        return mIdentityProvider;
    }

    public CredentialsManager(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String prefIdentProv = mPreferences.getString("mIdentityProvider", null);
        if (prefIdentProv != null) {
            setIdentityProvider(IdentityProvider.valueOf(prefIdentProv));
        }

        String pref = mPreferences.getString("mServerAPICredentials", null);
        if (pref != null) {
            setServerAPICredentials(ServerAPICredentials.fromString(pref));
        } else {
            setServerAPICredentials(null);
        }
        setState(pref == null ? State.LOGGED_OUT : State.LOGGED_IN);
    }

    public State getState() {
        return mState;
    }

    void setState(State state) {
        mState = state;
        fireAuthenticationStatusChange(state);
    }

    public static CredentialsManager getInstance(Context context) {
        if (instance == null) {
            synchronized (CredentialsManager.class) {
                if (instance == null) {
                    instance = new CredentialsManager(context);
                }
            }
        }
        return instance;
    }

    public void onLogInDone(IdentityProvider provider, ServerAPICredentials data, UserProperties userProperties) {
        setIdentityProvider(provider);
        setServerAPICredentials(data);

        setState(State.LOGGED_IN);
        fireAuthenticated(provider, data, userProperties);
    }

    public void setServerAPICredentials(ServerAPICredentials serverAPICredentials) {
        mServerAPICredentials = serverAPICredentials;
        if (serverAPICredentials != null) {
            mPreferences.edit().putString("mServerAPICredentials", mServerAPICredentials.toString()).commit();
        } else {
            mPreferences.edit().remove("mServerAPICredentials").commit();
        }
    }

    public void setIdentityProvider(IdentityProvider identityProvider) {
        mIdentityProvider = identityProvider;
        if (identityProvider != null) {
            mPreferences.edit().putString("mIdentityProvider", mIdentityProvider.name()).commit();
        } else {
            mPreferences.edit().remove("mIdentityProvider").commit();
        }
    }

    public void onLogOutDone() {
        setIdentityProvider(null);
        setServerAPICredentials(null);
        setState(State.LOGGED_OUT);
    }

    public void logIn(SingleFragmentActivity activity, boolean silent, IdentityProvider identityProvider) {
        setState(State.LOGGING_IN);
        setIdentityProvider(identityProvider);
        mIdentityProvider.startLogIn(activity, silent);
    }

    public void onLogInCancelled() {
        setState(State.LOGGED_OUT);
    }

    public interface Listener {
        void onAuthenticationStatusChange(State currentState);

        void onAuthenticated(IdentityProvider provider, ServerAPICredentials data, UserProperties userProperties);
    }

    private final List<Listener> mListeners = new ArrayList<Listener>();

    public static enum State {
        LOGGED_IN(true, false),
        LOGGING_OUT(true, true),
        LOGGED_OUT(false, false),
        LOGGING_IN(false, true);

        private final boolean working;
        private final boolean loggedIn;

        private State(boolean loggedIn, boolean working) {
            this.loggedIn = loggedIn;
            this.working = working;
        }

        public boolean isWorking() {
            return working;
        }

        public boolean isLoggedIn() {
            return loggedIn;
        }
    }

    public void logOut(SingleFragmentActivity activity, boolean revokeAccess) {
        setState(State.LOGGING_OUT);
        mIdentityProvider.startLogOut(activity, revokeAccess);
    }

    public void addListener(Listener listener) {
        synchronized (mListeners) {
            if (!mListeners.contains(listener)) {
                mListeners.add(listener);
            }
        }
    }

    public void removeListener(Listener listener) {
        synchronized (mListeners) {
            mListeners.remove(listener);
        }
    }

    private void fireAuthenticationStatusChange(State state) {
        synchronized (mListeners) {
            for (Listener listener : mListeners) {
                listener.onAuthenticationStatusChange(state);
            }
        }
    }

    private void fireAuthenticated(IdentityProvider provider, ServerAPICredentials data, UserProperties userProperties) {
        synchronized (mListeners) {
            for (Listener listener : mListeners) {
                listener.onAuthenticated(provider, data, userProperties);
            }
        }
    }

    public synchronized UserKey getCurrentUser() {
        if (mPreferences.contains(CURRENT_USER_ID)) {
            long currentUserId = mPreferences.getLong(CURRENT_USER_ID, 0);
            LogUtil.d(CredentialsManager.class.getName(), "Getting " + CURRENT_USER_ID + " = " + currentUserId);
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
            LogUtil.d(CredentialsManager.class.getName(), "Getting " + CURRENT_USER_ID + " = " + defaultUserId + " (fallback)");
            return new ObjectIdentifierBean(defaultUserId);
        }
    }

    public synchronized void setCurrentUser(UserKey userKey) {
        boolean commit = mPreferences.edit().putLong(CURRENT_USER_ID, userKey.getId()).commit();
        Log.i(CredentialsManager.class.getName(), "Setting " + CURRENT_USER_ID + " = " + userKey.getId() + " (success: " + commit + ")");
    }
}
