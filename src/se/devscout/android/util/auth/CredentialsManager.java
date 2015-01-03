package se.devscout.android.util.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.android.model.ObjectIdentifierBean;
import se.devscout.android.util.IdentityProvider;
import se.devscout.android.util.LogUtil;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.model.UserKey;
import se.devscout.server.api.model.UserProperties;

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

    public CredentialsManager(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public State getState() {
        return mState;
    }

    public void setState(State state) {
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

    public void onLogInDone(IdentityProvider provider, String data, UserProperties userProperties) {
        setState(State.LOGGED_IN);
        fireAuthenticated(provider, data, userProperties);
    }

    public void onLogOutDone() {
        setState(State.LOGGED_OUT);
    }

    public void logInUsingGoogle(SingleFragmentActivity activity) {
        logIn(new GoogleAuthenticationStrategy(this, activity));
    }

    private void logIn(AuthenticationStrategy strategy) {
        if (mAuthStrategy != null) {
            mAuthStrategy.startLogOut(false);
        }
        mAuthStrategy = strategy;
        setState(State.LOGGING_IN);
        strategy.startLogIn();
    }

    public void onLogInCancelled() {
        setState(State.LOGGED_OUT);
    }

    public interface Listener {
        void onAuthenticationStatusChange(State currentState);

        void onAuthenticated(IdentityProvider provider, String data, UserProperties userProperties);
    }

    private List<Listener> mListeners = new ArrayList<Listener>();

    AuthenticationStrategy mAuthStrategy;

    public static enum State {
        LOGGED_IN(true, false),
        LOGGING_OUT(true, true),
        LOGGED_OUT(false, false),
        LOGGING_IN(false, true);

        private boolean working;
        private boolean loggedIn;

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

    public void onActivityStop(SingleFragmentActivity activity) {
        if (mAuthStrategy != null) {
            mAuthStrategy.onActivityStop(activity);
        }
    }

    public void onActivityStart(SingleFragmentActivity activity) {
        if (mAuthStrategy != null) {
            mAuthStrategy.onActivityStart(activity);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data, SingleFragmentActivity activity) {
        if (mAuthStrategy != null) {
            mAuthStrategy.onActivityResult(requestCode, resultCode, data, activity);
        }
    }

    public void logOut(boolean revokeAccess) {
        if (mAuthStrategy != null) {
            setState(State.LOGGING_OUT);
            mAuthStrategy.startLogOut(revokeAccess);
        }
    }

    public void addListener(Listener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    private void fireAuthenticationStatusChange(State state) {
        for (Listener listener : mListeners) {
            listener.onAuthenticationStatusChange(state);
        }
    }

    private void fireAuthenticated(IdentityProvider provider, String data, UserProperties userProperties) {
        for (Listener listener : mListeners) {
            listener.onAuthenticated(provider, data, userProperties);
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
