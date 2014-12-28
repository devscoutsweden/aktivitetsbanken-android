package se.devscout.android.util.auth;

import android.content.Intent;
import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.android.util.IdentityProvider;
import se.devscout.server.api.model.UserProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton for keeping track of user authentication, meaning that it is in
 * charge of knowing whether or not a user is signed in. It also "manages" the
 * authentication process using an AuthenticationStrategy.
 */
public class CredentialsManager {

    private static CredentialsManager instance = null;
    private State mState = State.LOGGED_OUT;

    public State getState() {
        return mState;
    }

    public void setState(State state) {
        mState = state;
        fireAuthenticationStatusChange(state);
    }

    public static CredentialsManager getInstance() {
        if (instance == null) {
            synchronized (CredentialsManager.class) {
                if (instance == null) {
                    instance = new CredentialsManager();
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
            mAuthStrategy.startLogOut();
        }
        mAuthStrategy = strategy;
        setState(State.LOGGING_IN);
        strategy.startLogIn();
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

    public void logOut() {
        if (mAuthStrategy != null) {
            setState(State.LOGGING_OUT);
            mAuthStrategy.startLogOut();
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
}
