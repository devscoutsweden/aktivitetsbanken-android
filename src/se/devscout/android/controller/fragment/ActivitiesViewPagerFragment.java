package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import se.devscout.android.R;
import se.devscout.android.model.ObjectIdentifierPojo;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.ActivityUtil;
import se.devscout.android.util.ResourceUtil;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.model.Activity;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.ActivityRevision;
import se.devscout.server.api.model.Media;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesViewPagerFragment extends ActivityBankFragment implements ViewPager.OnPageChangeListener {

    protected ArrayList<ObjectIdentifierPojo> mActivities;
    protected int mSelectedIndex;

    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            /*
             * Restore fields from saved state, for example after the device has been rotated.
             */
            mActivities = (ArrayList<ObjectIdentifierPojo>) savedInstanceState.getSerializable("mActivities");
            mSelectedIndex = savedInstanceState.getInt("mSelectedIndex");
//            Log.d(ActivitiesListFragment.class.getName(), "State (e.g. search result) has been restored.");
        }

        setHasOptionsMenu(true);

        mViewPager = (ViewPager) inflater.inflate(R.layout.view_pager, container, false);
        mViewPager.setOnPageChangeListener(this);
        FragmentStatePagerAdapter pageAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                Activity activity = getActivities().get(i);
                SimpleDocumentFragment fragment = SimpleDocumentFragment.create();

                ActivityRevision revision = ActivityUtil.getLatestActivityRevision(activity);

                ResourceUtil resourceUtil = new ResourceUtil(getActivity());

                if (revision.getCoverMedia() != null) {
                    fragment.addImage(resourceUtil.toResourceId(revision.getCoverMedia().getURI()), true);
                }
                fragment
                        .addHeaderAndText(R.string.activity_introduction, revision.getDescriptionIntroduction())
                        .addHeaderAndText(R.string.activity_tab_material, revision.getDescriptionMaterial())
                        .addHeaderAndText(R.string.activity_preparations, revision.getDescriptionPreparation())
                        .addHeaderAndText(R.string.activity_how_to_do, revision.getDescription())
                        .addHeaderAndText(R.string.activity_safety, revision.getDescriptionSafety())
                        .addHeaderAndText(R.string.activity_notes, revision.getDescriptionNotes());

                if (!revision.getMediaItems().isEmpty()) {
                    fragment.addHeader(R.string.activity_tab_photos);
                    for (Media media : revision.getMediaItems()) {
                        fragment.addImage(resourceUtil.toResourceId(media.getURI()), false);
                    }
                }

                return fragment;
            }

            @Override
            public int getCount() {
                return getActivities().size();
            }
        };
        mViewPager.setAdapter(pageAdapter);
        mViewPager.setCurrentItem(mSelectedIndex);
        updateActivityTitle(mSelectedIndex);

        return mViewPager;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity, menu);
        initFavouriteMenuItem(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initFavouriteMenuItem(Menu menu) {
        MenuItem item = menu.findItem(R.id.activityMenuFavourite);
        Activity activity = getSelectedActivity();
        boolean favourite = ActivityBankFactory.getInstance(getActivity()).isFavourite(activity, null);
        item.setIcon(favourite ? R.drawable.ic_action_important : R.drawable.ic_action_not_important);
    }

    private Activity getSelectedActivity() {
        return getActivities().get(mSelectedIndex);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.activityMenuFavourite) {
            ActivityBank activityBank = ActivityBankFactory.getInstance(getActivity());
            boolean favourite = activityBank.isFavourite(getSelectedActivity(), null);
            if (favourite) {
                activityBank.unsetFavourite(getSelectedActivity(), null);
            } else {
                activityBank.setFavourite(getSelectedActivity(), null);
            }
            item.setIcon(!favourite ? R.drawable.ic_action_important : R.drawable.ic_action_not_important);
        }
        return super.onOptionsItemSelected(item);    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected ArrayList<Activity> getActivities() {
        ArrayList<Activity> activities = new ArrayList<Activity>();
        for (ObjectIdentifierPojo activity : mActivities) {
            //TODO: Save complete Activity objects in mActivities instead of only the keys? Performance gain or performance loss?
            activities.add(getActivityBank().readFull(activity));
        }
        return activities;
    }

    public static ActivitiesViewPagerFragment create(List<? extends ActivityKey> activities, int selectedIndex) {
        ActivitiesViewPagerFragment fragment = new ActivitiesViewPagerFragment();
        ArrayList<ObjectIdentifierPojo> activityKeys = new ArrayList<ObjectIdentifierPojo>();
        for (ActivityKey key : activities) {
            activityKeys.add(new ObjectIdentifierPojo(key.getId()));
        }
        fragment.mActivities = activityKeys;
        fragment.mSelectedIndex = selectedIndex;
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
         * Store fields into saved state, for example when the activity is destroyed after the device has been rotated.
         */
        Log.d(ActivitiesListFragment.class.getName(), "Saving state");
        outState.putSerializable("mActivities", mActivities);
        outState.putInt("mSelectedIndex", mSelectedIndex);
        Log.d(ActivitiesListFragment.class.getName(), "State saved");
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        updateActivityTitle(i);
        mSelectedIndex = i;
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    private void updateActivityTitle(int selectedActivityIndex) {
        getActivity().setTitle(ActivityUtil.getLatestActivityRevision(getActivities().get(selectedActivityIndex)).getName());
    }
}