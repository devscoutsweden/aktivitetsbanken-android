package se.devscout.android.controller.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import se.devscout.android.R;
import se.devscout.android.model.ObjectIdentifierBean;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.PreferencesUtil;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.model.ActivityKey;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesViewPagerFragment extends PagerFragment {

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity, menu);
        initFavouriteMenuItem(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initFavouriteMenuItem(Menu menu) {
        MenuItem item = menu.findItem(R.id.activityMenuFavourite);
        ActivityKey activityKey = getActivity(mSelectedIndex);
        Boolean isFavourite = ActivityBankFactory.getInstance(getActivity()).isFavourite(activityKey, PreferencesUtil.getInstance(getActivity()).getCurrentUser());
        if (isFavourite != null) {
            item.setIcon(isFavourite ? R.drawable.ic_action_important : R.drawable.ic_action_not_important);
        } else {
            item.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.activityMenuFavourite) {
            ActivityBank activityBank = ActivityBankFactory.getInstance(getActivity());
            ActivityKey activityKey = getActivity(mSelectedIndex);
            boolean favourite = activityBank.isFavourite(activityKey, PreferencesUtil.getInstance(getActivity()).getCurrentUser());
            if (favourite) {
                activityBank.unsetFavourite(activityKey, PreferencesUtil.getInstance(getActivity()).getCurrentUser());
            } else {
                activityBank.setFavourite(activityKey, PreferencesUtil.getInstance(getActivity()).getCurrentUser());
            }
            item.setIcon(!favourite ? R.drawable.ic_action_important : R.drawable.ic_action_not_important);
        }
        return super.onOptionsItemSelected(item);
    }

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

}
