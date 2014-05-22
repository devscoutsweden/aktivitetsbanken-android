package se.devscout.android.controller.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import se.devscout.android.R;
import se.devscout.android.controller.activity.drawer.*;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.server.api.ActivityBank;

abstract class SingleFragmentActivity<T extends Fragment> extends FragmentActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private FrameLayout mContentFrame;
    private ActionBarDrawerToggle mDrawerToggle;
    protected T mFragment;
    private DrawerListAdapter mDrawerListAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.start_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.start_drawer_list);
        mContentFrame = (FrameLayout) findViewById(R.id.start_content_frame);

        mDrawerListAdapter = new DrawerListAdapter(this);
        mDrawerListAdapter.add(
                new HeaderDrawerItem(getString(R.string.drawer_start_header)),
                new StartDrawerItem(getString(R.string.startTabHome), R.drawable.ic_action_favorite),
                new FavouriteActivitiesDrawerItem(getString(R.string.startTabFavourites), R.drawable.ic_action_important),
                new SearchDrawerItem(getString(R.string.startTabSearch), R.drawable.ic_action_search),
                new AgeGroupsDrawerItem(getString(R.string.startTabAgeGroups), R.drawable.ic_action_cc_bcc),
                new CategoriesDrawerItem(getString(R.string.startTabConcepts), R.drawable.ic_action_labels)
        );

        mDrawerList.setAdapter(mDrawerListAdapter);
        mDrawerList.setOnItemClickListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            private CharSequence mOldTitle;

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActionBar().setTitle(mOldTitle);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mOldTitle = getActionBar().getTitle();
                getActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

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
            // Hide all actions in action bar
        }
        return super.onPrepareOptionsPanel(view, menu);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.i(SingleFragmentActivity.class.getName(), "You clicked " + position);
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
