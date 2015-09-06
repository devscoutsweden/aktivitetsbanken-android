package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import se.devscout.android.R;
import se.devscout.android.model.ActivityKey;
import se.devscout.android.model.ObjectIdentifierBean;
import se.devscout.android.model.activityfilter.ActivityFilter;
import se.devscout.android.util.LogUtil;
import se.devscout.android.view.ActivitiesListItem;
import se.devscout.android.view.ActivitiesListView;

public class ActivitiesListFragment extends NonBlockingSearchResultFragment<ActivitiesListItem, ActivitiesListView> {
    private ActivitiesListView.Sorter mSortOrder;
    private ActivityFilter mFilter;
    private boolean mAddSearchHistory;

    public ActivitiesListFragment() {
        super();
    }

    public void setArguments(ActivityFilter filter, ActivitiesListView.Sorter sortOrder, boolean addSearchHistory) {
        final Bundle args = new Bundle();
        fillBundle(args, filter, sortOrder, addSearchHistory);
        setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initArguments(getArguments());
        initArguments(savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initArguments(Bundle bundle) {
        if (bundle != null) {
            /*
             * Restore fields from saved state, for example after the device has been rotated.
             */
            mSortOrder = (ActivitiesListView.Sorter) bundle.getSerializable("mSortOrder");
            mFilter = (ActivityFilter) bundle.getSerializable("mFilter");
            mAddSearchHistory = bundle.getBoolean("mAddSearchHistory");
            LogUtil.d(ActivitiesListFragment.class.getName(), "State (e.g. search result) has been restored.");
        }
    }

    private void fillBundle(Bundle args, ActivityFilter filter, ActivitiesListView.Sorter sortOrder, boolean addSearchHistory) {
        args.putSerializable("mFilter", filter);
        args.putSerializable("mSortOrder", sortOrder);
        args.putBoolean("mAddSearchHistory", addSearchHistory);
    }

    @Override
    protected ActivitiesListView createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new ActivitiesListView(getActivity(), R.string.searchResultEmptyMessage, R.string.searchResultEmptyTitle, mFilter, mSortOrder, false, mAddSearchHistory);
    }

    public void setSortOrder(ActivitiesListView.Sorter sortOrder) {
        mSortOrder = sortOrder;

        //TODO: Really assume that first child is the one of interest?
        ActivitiesListView activitiesListView = (ActivitiesListView) ((ViewGroup) getView()).getChildAt(0);
        activitiesListView.setSortOrder(sortOrder);
    }

    public ActivitiesListView.Sorter getSortOrder() {
        return getArguments().getString("sortOrder", null) != null ? ActivitiesListView.Sorter.valueOf(getArguments().getString("sortOrder", null)) : null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
         * Store fields into saved state, for example when the activity is destroyed after the device has been rotated.
         */
        LogUtil.d(ActivitiesListFragment.class.getName(), "Saving state");
        fillBundle(outState, mFilter, mSortOrder, mAddSearchHistory);
        LogUtil.d(ActivitiesListFragment.class.getName(), "State saved");
    }

    public static ActivitiesListFragment create(List<ActivityKey> activities, ActivitiesListView.Sorter defaultSortOrder) {
        ActivitiesListFragment fragment = new ActivitiesListFragment();
        ArrayList<ObjectIdentifierBean> sortedList = new ArrayList<ObjectIdentifierBean>();
        for (ActivityKey key : activities) {
            sortedList.add(new ObjectIdentifierBean(key.getId()));
        }
        fragment.mSortOrder = defaultSortOrder;
        return fragment;
    }

    public static ActivitiesListFragment create(ActivityFilter filter, ActivitiesListView.Sorter defaultSortOrder, boolean addSearchHistory) {
        final ActivitiesListFragment fragment = new ActivitiesListFragment();
        fragment.setArguments(filter, defaultSortOrder, addSearchHistory);
        return fragment;
    }
}
