package se.devscout.android.controller.activity;

import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.ListView;
import android.widget.Toast;
import se.devscout.android.R;
import se.devscout.android.controller.activity.drawer.*;
import se.devscout.android.controller.fragment.AbstractActivityBankListener;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.auth.CredentialsManager;
import se.devscout.android.util.concurrency.BackgroundTasksHandlerThread;
import se.devscout.android.view.widget.ComponentFactoryRepo;
import se.devscout.android.view.widget.TabComponentFactory;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.model.SearchHistory;

public abstract class SingleFragmentActivity<T extends Fragment> extends FragmentActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    //    private FrameLayout mContentFrame;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected T mFragment;
    private DrawerListAdapter mDrawerListAdapter;
    private AbstractActivityBankListener mActivityBankListener;

    //    private CredentialsManager mCredentialsManager;
    private AbstractActivityBankListener mActivityBackAsyncExceptionListener;

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
        CredentialsManager.getInstance(this).onActivityStop(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        CredentialsManager.getInstance(this).onActivityStart(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mActivityBackAsyncExceptionListener != null) {
            getActivityBank().removeListener(mActivityBackAsyncExceptionListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mActivityBackAsyncExceptionListener == null) {
            mActivityBackAsyncExceptionListener = new AbstractActivityBankListener() {
                @Override
                public void onServiceDegradation(final String message, final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SingleFragmentActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            };
        }
        getActivityBank().addListener(mActivityBackAsyncExceptionListener);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_fragment_activity);

//        if (!CredentialsManager.getInstance().getState().isLoggedIn()) {
//            mCredentialsManager = new CredentialsManager();
//        }

//        if (mCredentialsManager != null) {
//            mCredentialsManager.onActivityCreate(this);
//        }

/*
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
*/

        mDrawerLayout = (DrawerLayout) findViewById(R.id.start_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.start_drawer_list);
//        mContentFrame = (FrameLayout) findViewById(R.id.start_content_frame);

        mDrawerListAdapter = new DrawerListAdapter(this);
        mDrawerListAdapter.add(new HeaderDrawerItem(getString(R.string.drawer_start_header)));
        for (TabComponentFactory finder : ComponentFactoryRepo.getInstance(this).getTabFactories()) {
            mDrawerListAdapter.add(createFragmentCreatorDrawerItem(finder));
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

/*
    public void showLogIn() {
        View view = findViewById(R.id.start_login_container);
        view.setVisibility(View.VISIBLE);
    }
*/

    private FragmentCreatorDrawerItem createFragmentCreatorDrawerItem(TabComponentFactory finder) {
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
        CredentialsManager.getInstance(this).onActivityResult(requestCode, resultCode, data, this);
    }

/*
    public void signInWithGplus() {
        mCredentialsManager.signInWithGplus();
    }

    public void signOutFromGplus() {
        mCredentialsManager.logOut();
    }
*/
}
