package se.devscout.android.view;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import se.devscout.android.util.ActivityBankFactory;

import java.util.ArrayList;
import java.util.List;

public class AnonymousUserFactory {
    private static AnonymousUserFactory INSTANCE;
    private AsyncTask<Void, Void, Boolean> mCreateAnonymousUserTask;
    private List<AnonymousUserFactoryListener> mListeners = new ArrayList<AnonymousUserFactoryListener>();

    public static synchronized AnonymousUserFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AnonymousUserFactory();
        }
        return INSTANCE;
    }

    private AnonymousUserFactory() {

    }

    public synchronized void createAnonymousUser(AnonymousUserFactoryListener listener, final Context context) {
        if (mCreateAnonymousUserTask == null) {
            mCreateAnonymousUserTask = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... voids) {
                    try {
                        Log.d(AnonymousUserFactory.class.getName(), "Instructing activity bank to create anonymous API user");
                        return ActivityBankFactory.getInstance(context).createAnonymousAPIUser();
                    } catch (Throwable e) {
                        Log.e(AnonymousUserFactory.class.getName(), "Exception when creating anonymous API user.", e);
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean success) {
                    fireAnonymousUserCreated(success);
                }
            }.execute();
        }
        Log.d(AnonymousUserFactory.class.getName(), "Adding AnonymousUserFactoryListener");
        mListeners.add(listener);
    }

    private synchronized void fireAnonymousUserCreated(boolean success) {
        for (AnonymousUserFactoryListener listener : mListeners) {
            Log.d(AnonymousUserFactory.class.getName(), "Alerting AnonymousUserFactoryListener");
            listener.onAnonymousUserCreated(success);
        }
        mCreateAnonymousUserTask = null;
    }
}
