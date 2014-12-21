package se.devscout.android.controller.activity;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import se.devscout.android.R;
import se.devscout.android.controller.activity.drawer.*;
import se.devscout.android.controller.fragment.AbstractActivityBankListener;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.IdentityProvider;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.concurrency.BackgroundTasksHandlerThread;
import se.devscout.android.view.AbstractActivitiesFinderComponentFactory;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.model.SearchHistory;

import java.io.IOException;

public abstract class SingleFragmentActivity<T extends Fragment> extends FragmentActivity implements AdapterView.OnItemClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;
    private static final String WEB_APP_CLIENT_ID = "551713736410-q55omfobgs9j8ia4ae3r7sbi20vcvt49.apps.googleusercontent.com";
    private static final int RC_CHOOSE_ACCOUNT = 1000;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private FrameLayout mContentFrame;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected T mFragment;
    private DrawerListAdapter mDrawerListAdapter;
    private AbstractActivityBankListener mActivityBankListener;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private ConnectionResult mConnectionResult;
    private boolean mSignInClicked;

    protected SingleFragmentActivity() {
        LogUtil.initExceptionLogging(this);
    }

    private BackgroundTasksHandlerThread mBackgroundTasksHandlerThread;

    public synchronized BackgroundTasksHandlerThread getBackgroundTasksHandlerThread() {
        if (mBackgroundTasksHandlerThread == null) {
            mBackgroundTasksHandlerThread = new BackgroundTasksHandlerThread(this, new Handler());
            mBackgroundTasksHandlerThread.start();
            mBackgroundTasksHandlerThread.getLooper();

        }
        return mBackgroundTasksHandlerThread;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_drawer);

        mGoogleApiClient = new GoogleApiClient.Builder(this, this, this)
                .addApi(Plus.API)
//                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.start_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.start_drawer_list);
        mContentFrame = (FrameLayout) findViewById(R.id.start_content_frame);

        mDrawerListAdapter = new DrawerListAdapter(this, getSupportFragmentManager(), R.id.start_content_frame);
        mDrawerListAdapter.add(new HeaderDrawerItem(getString(R.string.drawer_start_header)));
        for (AbstractActivitiesFinderComponentFactory finder : AbstractActivitiesFinderComponentFactory.getActivitiesFinders()) {
            if (finder.isFragmentCreator()) {
                mDrawerListAdapter.add(createFragmentCreatorDrawerItem(finder));
            }
        }

        mDrawerListAdapter.addSearchHistory(getString(R.string.drawer_search_history_header), getActivityBank());
        mActivityBankListener = new AbstractActivityBankListener() {
            @Override
            public void onSearchHistoryItemAdded(SearchHistory searchHistory) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDrawerListAdapter.loadSearchHistoryItems(getActivityBank());
                    }
                });
            }
        };
//        getActivityBank().addListener(mActivityBankListener);

        mDrawerList.setAdapter(mDrawerListAdapter);
        mDrawerList.setOnItemClickListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            private CharSequence mOldTitle;

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                setTitle(mOldTitle);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);    //To change body of overridden methods use File | Settings | File Templates.
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);    //To change body of overridden methods use File | Settings | File Templates.
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                if (mIsSearchHistoryUpdated) {
//                    mDrawerListAdapter.loadSearchHistoryItems(getActivityBank());
//                    mIsSearchHistoryUpdated = false;
//                }
                mOldTitle = getActionBar().getTitle();
                setTitle(R.string.app_name);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(false);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        FragmentManager fm = getSupportFragmentManager();

        mFragment = (T) fm.findFragmentById(R.id.start_content_frame);
        if (mFragment == null) {
            mFragment = createFragment();
            fm.beginTransaction().add(R.id.start_content_frame, mFragment).commit();
        }

        if (savedInstanceState != null) {
            Fragment.SavedState state = (Fragment.SavedState) savedInstanceState.getParcelable("state");
            if (state != null) {
                mFragment.setInitialSavedState(state);
            }
        }
    }

    private FragmentCreatorDrawerItem createFragmentCreatorDrawerItem(AbstractActivitiesFinderComponentFactory finder) {
        int titleResId = finder.getTitleResId();
        String title = titleResId > 0 ? getString(titleResId) : "";
        int iconResId = finder.getIconResId();
        return new FragmentCreatorDrawerItem(title, iconResId, finder);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBackgroundTasksHandlerThread != null) {
            mBackgroundTasksHandlerThread.close();
            mBackgroundTasksHandlerThread.quit();
        }
        if (mActivityBankListener != null) {
            getActivityBank().removeListener(mActivityBankListener);
        }
    }

    protected ActivityBank getActivityBank() {
        return ActivityBankFactory.getInstance(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.start_content_frame);
        if (fragment == null) {
            Fragment.SavedState state = fm.saveFragmentInstanceState(fragment);
            outState.putParcelable("state", state);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if (drawerOpen) {
            // Hide (remove) all actions in action bar. Items will be recreated when menu is invalidated the next time.
            menu.clear();
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        LogUtil.i(SingleFragmentActivity.class.getName(), "You clicked " + position);
        DrawerItem drawerItem = mDrawerListAdapter.getItem(position);

        if (drawerItem instanceof ExecutableDrawerItem) {
            ExecutableDrawerItem creator = (ExecutableDrawerItem) drawerItem;

            creator.run(this);

            setTitle(drawerItem.getTitle());
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    protected abstract T createFragment();

    /**
     * Because the resolution for the connection failure was started with startIntentSenderForResult and the code RC_SIGN_IN, we can capture the result inside Activity.onActivityResult.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else if (requestCode == RC_CHOOSE_ACCOUNT) {
            if (resultCode == RESULT_OK) {
                final String eMail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                if (eMail != null) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            try {
                                String scope = "audience:server:client_id:" + WEB_APP_CLIENT_ID;
                                String token = GoogleAuthUtil.getToken(SingleFragmentActivity.this, eMail, scope);
                                if (token != null) {
                                    IdentityProvider provider = IdentityProvider.GOOGLE;
                                    getActivityBank().logIn(provider, token);
        //                            SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(SingleFragmentActivity.this).edit();
        //                            prefEditor.putString("server_api_auth_data", token);
        //                            prefEditor.putString("server_api_auth_type", "google");
        //                            prefEditor.commit();
                                }
                            } catch (IOException e) {
                                LogUtil.e(SingleFragmentActivity.class.getName(), "Could not get Google ID token", e);
                            } catch (GoogleAuthException e) {
                                LogUtil.e(SingleFragmentActivity.class.getName(), "Could not get Google ID token", e);
                            }
                            return null;
                        }
                    }.execute();
                } else {
                    startChooseAccountActivity();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.pick_account, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        if (person != null) {
            String displayName = person.getDisplayName();
            String language = person.getLanguage();
            String id = person.getId();
            String url = person.getImage() != null ? person.getImage().getUrl() : "";
            LogUtil.i(SingleFragmentActivity.class.getName(), "" +
                    "Google+ Profile Information:" +
                    "\nDisplay Name: " + displayName +
                    "\nLanguage:     " + language +
                    "\nId:           " + id +
                    "\nImage URL:    " + url);
            Toast.makeText(this, "User " + displayName + " is connected!", Toast.LENGTH_LONG).show();

            startChooseAccountActivity();
        }
    }

    private void startChooseAccountActivity() {
        startActivityForResult(AccountPicker.newChooseAccountIntent(null, null, new String[] {"com.google"}, false, null, null, null, null), RC_CHOOSE_ACCOUNT);
    }

    /**
     * Google Play services will trigger the onConnectionSuspended callback if our Activity loses its service connection. Typically you will want to attempt to reconnect when this happens in order to retrieve a new ConnectionResult that can be resolved by the user.
     *
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    /**
     * Sign-in into google
     */
    public void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
//                startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }
}
