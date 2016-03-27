package se.devscout.android.controller.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import se.devscout.android.R;
import se.devscout.android.controller.activity.drawer.DrawerItem;
import se.devscout.android.controller.activity.drawer.DrawerListAdapter;
import se.devscout.android.controller.activity.drawer.ExecutableDrawerItem;
import se.devscout.android.controller.activity.drawer.FragmentCreatorDrawerItem;
import se.devscout.android.controller.activity.drawer.HeaderDrawerItem;
import se.devscout.android.model.ActivityKey;
import se.devscout.android.model.SearchHistory;
import se.devscout.android.model.UserKey;
import se.devscout.android.model.repo.ActivityBank;
import se.devscout.android.model.repo.ActivityBankListener;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.concurrency.BackgroundTasksHandlerThread;
import se.devscout.android.view.widget.ComponentFactoryRepo;
import se.devscout.android.view.widget.TabComponentFactory;

public abstract class SingleFragmentActivity<T extends Fragment> extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    //    private FrameLayout mContentFrame;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected T mFragment;
    private DrawerListAdapter mDrawerListAdapter;
    private ActivityBankListener mActivityBankListener;

    private ActivityBankListener mActivityBackAsyncExceptionListener;

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
            mActivityBackAsyncExceptionListener = new ActivityBankListener() {
                @Override
                public void onSearchHistoryItemAdded(SearchHistory searchHistory) {
                }

                @Override
                public void onFavouriteChange(ActivityKey activityKey, UserKey userKey, boolean isFavouriteNow) {
                }

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

        mDrawerLayout = (DrawerLayout) findViewById(R.id.start_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.start_drawer_list);
//        mContentFrame = (FrameLayout) findViewById(R.id.start_content_frame);

        mDrawerListAdapter = new DrawerListAdapter(this);
        mDrawerListAdapter.add(new HeaderDrawerItem(getString(R.string.drawer_start_header)));
        for (TabComponentFactory finder : ComponentFactoryRepo.getInstance(this).getTabFactories()) {
            mDrawerListAdapter.add(createFragmentCreatorDrawerItem(finder));
        }

        mDrawerListAdapter.addSearchHistory(getString(R.string.drawer_search_history_header), getActivityBank());
        mActivityBankListener = new ActivityBankListener() {
            @Override
            public void onSearchHistoryItemAdded(SearchHistory searchHistory) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDrawerListAdapter.loadSearchHistoryItems(getActivityBank());
                    }
                });
            }

            @Override
            public void onFavouriteChange(ActivityKey activityKey, UserKey userKey, boolean isFavouriteNow) {
            }

            @Override
            public void onServiceDegradation(String message, Exception e) {
            }
        };
//        getActivityBank().addListener(mActivityBankListener);

        mDrawerList.setAdapter(mDrawerListAdapter);
        mDrawerList.setOnItemClickListener(this);

        // Get a support ActionBar corresponding to this toolbar

        // Enable the Up button
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);

//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setHomeButtonEnabled(true);

        FragmentManager fm = getSupportFragmentManager();

        mFragment = (T) fm.findFragmentById(R.id.start_content_frame);
        if (mFragment == null) {
            mFragment = createFragment();
            fm.beginTransaction().add(R.id.start_content_frame, mFragment).commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Use no-toolbar constructor because of what is described on http://stackoverflow.com/questions/26588917/appcompat-v7-toolbar-onoptionsitemselected-not-called
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(false);

        if (savedInstanceState != null) {
            Fragment.SavedState state = (Fragment.SavedState) savedInstanceState.getParcelable("state");
            if (state != null) {
                mFragment.setInitialSavedState(state);
            }
        }
    }

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
    public void onBackPressed() {
        finish();
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

}
