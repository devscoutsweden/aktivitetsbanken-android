package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import se.devscout.android.R;
import se.devscout.android.model.ObjectIdentifierBean;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.PreferencesUtil;
import se.devscout.android.view.ViewPagerIndicator;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.model.ActivityKey;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesViewPagerFragment extends ActivityBankFragment implements ViewPager.OnPageChangeListener {

    protected ArrayList<ObjectIdentifierBean> mActivityKeys;
    protected ArrayList<String> mActivityTitles;
    protected int mSelectedIndex;

    private ViewPager mViewPager;
    private ViewPagerIndicator mViewPagerIndicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            /*
             * Restore fields from saved state, for example after the device has been rotated.
             */
            mActivityKeys = (ArrayList<ObjectIdentifierBean>) savedInstanceState.getSerializable("mActivityKeys");
            mActivityTitles = (ArrayList<String>) savedInstanceState.getSerializable("mActivityTitles");
            mSelectedIndex = savedInstanceState.getInt("mSelectedIndex");
//            LogUtil.d(ActivitiesListFragment.class.getName(), "State (e.g. search result) has been restored.");
        }

        setHasOptionsMenu(true);

        final View view = inflater.inflate(R.layout.view_pager, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mViewPagerIndicator = (ViewPagerIndicator) view.findViewById(R.id.viewPagerIndicator);
        mViewPagerIndicator.setVisibility(mActivityKeys.size() > 1 ? View.VISIBLE : View.GONE);
        mViewPagerIndicator.setCount(mActivityKeys.size());
        mViewPager.setOnPageChangeListener(this);
        FragmentStatePagerAdapter pageAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                ActivityKey key = mActivityKeys.get(i);
//                prefetch(i);
                ActivityFragment activityFragment = ActivityFragment.create(key);
                return activityFragment;
            }

/*
            private void prefetch(int index) {
                BackgroundTasksHandlerThread handlerThread = ((SingleFragmentActivity) view.getRootView().getContext()).getBackgroundTasksHandlerThread();
                ActivityBank activityBank = getActivityBank();

                activityBank.readActivityAsync(mActivityKeys.get(index), null, handlerThread);
                for (int prefetchIndex = 0; prefetchIndex < 5; prefetchIndex++) {
                    if (index - prefetchIndex >= 0) {
                        activityBank.readActivityAsync(mActivityKeys.get(index - prefetchIndex), null, handlerThread);
                    }
                    if (index + prefetchIndex < mActivityKeys.size()) {
                        activityBank.readActivityAsync(mActivityKeys.get(index + prefetchIndex), null, handlerThread);
                    }
                }
            }
*/

            @Override
            public int getCount() {
                return mActivityKeys.size();
            }
        };
        mViewPager.setAdapter(pageAdapter);
        mViewPager.setCurrentItem(mSelectedIndex);
        updateActivityTitle(mSelectedIndex);

        return view;
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
        return mActivityKeys.get(index);
    }

    public static ActivitiesViewPagerFragment create(List<? extends ActivityKey> activities, ArrayList<String> titles, int selectedIndex) {
        ActivitiesViewPagerFragment fragment = new ActivitiesViewPagerFragment();
        ArrayList<ObjectIdentifierBean> activityKeys = new ArrayList<ObjectIdentifierBean>();
        for (ActivityKey key : activities) {
            activityKeys.add(new ObjectIdentifierBean(key.getId()));
        }
        fragment.mActivityKeys = activityKeys;
        fragment.mActivityTitles = titles;
        fragment.mSelectedIndex = selectedIndex;
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
         * Store fields into saved state, for example when the activity is destroyed after the device has been rotated.
         */
        LogUtil.d(ActivitiesListFragment.class.getName(), "Saving state");
        outState.putSerializable("mActivityKeys", mActivityKeys);
        outState.putSerializable("mActivityTitles", mActivityTitles);
        outState.putInt("mSelectedIndex", mSelectedIndex);
        LogUtil.d(ActivitiesListFragment.class.getName(), "State saved");
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        updateActivityTitle(i);
        mSelectedIndex = i;
        mViewPagerIndicator.setSelectedIndex(mSelectedIndex);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    private void updateActivityTitle(int selectedActivityIndex) {
        getActivity().setTitle(mActivityTitles.get(selectedActivityIndex));
    }
}
