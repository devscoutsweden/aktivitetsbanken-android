package se.devscout.android.controller.fragment;

import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import se.devscout.android.model.ActivityHistoryPropertiesBean;
import se.devscout.android.model.ObjectIdentifierBean;
import se.devscout.android.util.auth.CredentialsManager;
import se.devscout.android.util.concurrency.BackgroundTask;
import se.devscout.android.util.concurrency.BackgroundTasksHandlerThread;
import se.devscout.server.api.model.*;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesViewPagerFragment extends PagerFragment implements BackgroundTasksHandlerThread.Listener {

    private static final int RECORD_ACTIVITY_IN_HISTORY_DELAY = 2000;
    /**
     * Used to indicate that the current activity was not loaded when the
     * add-activity-to-viewing-history timer was triggered the first time.
     */
    private ActivityKey mCurrentActivityKey;
    private CountDownTimer mTimer;

    protected FragmentStatePagerAdapter createPagerAdapter() {
        return new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                ActivityKey key = mKeys.get(i);
//                prefetch(i);
                ActivityFragment activityFragment = ActivityFragment.create(key);
                return activityFragment;
            }

/*
            private void prefetch(int index) {
                BackgroundTasksHandlerThread handlerThread = ((SingleFragmentActivity) view.getRootView().getContext()).getBackgroundTasksHandlerThread();
                ActivityBank activityBank = getActivityBank();

                activityBank.readActivityAsync(mKeys.get(index), null, handlerThread);
                for (int prefetchIndex = 0; prefetchIndex < 5; prefetchIndex++) {
                    if (index - prefetchIndex >= 0) {
                        activityBank.readActivityAsync(mKeys.get(index - prefetchIndex), null, handlerThread);
                    }
                    if (index + prefetchIndex < mKeys.size()) {
                        activityBank.readActivityAsync(mKeys.get(index + prefetchIndex), null, handlerThread);
                    }
                }
            }
*/

            @Override
            public int getCount() {
                return mKeys.size();
            }
        };
    }

    @Override
    public void onPageSelected(int i) {
        // Start the timer which (once triggered) will added the activity to the viewing history.
        startTimer(getActivity(i));

        super.onPageSelected(i);    //To change body of overridden methods use File | Settings | File Templates.
    }

/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity, menu);
        initFavouriteMenuItem(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
*/

/*
    private void initFavouriteMenuItem(Menu menu) {
        MenuItem item = menu.findItem(R.id.activityMenuFavourite);
        ActivityKey activityKey = getActivity(mSelectedIndex);
        Boolean isFavourite = ActivityBankFactory.getInstance(getActivity()).isFavourite(activityKey, CredentialsManager.getInstance(getActivity()).getCurrentUser());
        if (isFavourite != null) {
            item.setIcon(isFavourite ? R.drawable.ic_action_important : R.drawable.ic_action_not_important);
        } else {
            item.setVisible(false);
        }
    }
*/

/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.activityMenuFavourite) {
            ActivityBank activityBank = ActivityBankFactory.getInstance(getActivity());
            ActivityKey activityKey = getActivity(mSelectedIndex);
            boolean favourite = activityBank.isFavourite(activityKey, CredentialsManager.getInstance(getActivity()).getCurrentUser());
            getBackgroundTasksHandlerThread(getActivity()).queueUpdateFavouriteStatus(activityKey, !favourite);
            item.setActionView(R.layout.menu_item_progress); // This will change the menu item to a progress bar, which will be shown until the command completes and the menu is invalidated (and subsequently recreated).
        }
        return super.onOptionsItemSelected(item);
    }
*/

    protected ActivityKey getActivity(int index) {
        return mKeys.get(index);
    }

    public static ActivitiesViewPagerFragment create(List<? extends ActivityKey> activities, ArrayList<String> titles, int selectedIndex) {
        ActivitiesViewPagerFragment fragment = new ActivitiesViewPagerFragment();
        ArrayList<ObjectIdentifierBean> activityKeys = new ArrayList<ObjectIdentifierBean>();
        for (ActivityKey key : activities) {
            activityKeys.add(new ObjectIdentifierBean(key.getId()));
        }
        fragment.mKeys = activityKeys;
        fragment.mTitles = titles;
        fragment.mSelectedIndex = selectedIndex;
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        getBackgroundTasksHandlerThread(getActivity()).addListener(this);
    }

    @Override
    public void onPause() {
        if (mTimer != null) {
            // The user has left the activity. Cancel the timer which will added the activity to the viewing history.
            mTimer.cancel();
            mTimer = null;
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        // Start the timer which (once triggered) will added the activity to the viewing history.
        startTimer(getActivity(mSelectedIndex));
        super.onResume();
    }

    @Override
    public void onStop() {
        getBackgroundTasksHandlerThread(getActivity()).removeListener(this);
        super.onStop();
    }

    @Override
    public BackgroundTasksHandlerThread.ListenerAction onDone(Object parameter, Object response, BackgroundTask task) {
        if (response instanceof ActivityList) {
            ActivityList activities = (ActivityList) response;
            if (mCurrentActivityKey != null) {
                // The mCurrentActivityKey field has been set and that means
                // that the first attempt to add the activity to the viewing
                // history failed because the activity hadn't been loaded yet.
                // Restart the timer now, when it has been loaded.
                Activity activity = activities.get(mCurrentActivityKey);
                if (activity != null) {
                    startTimer(mCurrentActivityKey);
                }
            }
        }

        return BackgroundTasksHandlerThread.ListenerAction.KEEP;
    }

    private void startTimer(final ActivityKey currentActivityKey) {
        if (mTimer != null) {
            // Cancel existing timer. It's just good manners.
            mTimer.cancel();
            mTimer = null;
        }

        mTimer = new CountDownTimer(RECORD_ACTIVITY_IN_HISTORY_DELAY, 1000) {

            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                try {
                    UserKey currentUser = CredentialsManager.getInstance(getActivity()).getCurrentUser();

                    ActivityHistoryPropertiesBean properties = new ActivityHistoryPropertiesBean(currentUser, new ActivityHistoryDataBean(currentActivityKey));

                    getActivityBank().createActivityHistory(properties, currentUser);
                    mCurrentActivityKey = null;
                } catch (Exception e) {
                    // A NullPointerException is thrown if the timeout occurs before the activity is even loaded (i.e. read from the server).
                    // Save the activity's key as an indicator that the timer should be restarted once the activity has actually been loaded.
                    mCurrentActivityKey = currentActivityKey;
                }
            }
        };
        mTimer.start();
    }

}
