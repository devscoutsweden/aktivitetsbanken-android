package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.model.ObjectIdentifierBean;
import se.devscout.android.view.ActivitiesListItem;
import se.devscout.android.view.ActivitiesListView;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.ActivityKey;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesListFragment extends NonBlockingSearchResultFragment<ActivitiesListItem, ActivitiesListView> {
    private ActivitiesListView.Sorter mSortOrder;
    private ActivityFilter mFilter;

    public ActivitiesListFragment() {
        super();
    }

    public ActivitiesListFragment(ActivityFilter filter, ActivitiesListView.Sorter sortOrder) {
        mFilter = filter;
        mSortOrder = sortOrder;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            /*
             * Restore fields from saved state, for example after the device has been rotated.
             */
            mSortOrder = (ActivitiesListView.Sorter) savedInstanceState.getSerializable("mSortOrder");
            Log.d(ActivitiesListFragment.class.getName(), "State (e.g. search result) has been restored.");
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected ActivitiesListView createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new ActivitiesListView(getActivity(), R.string.searchResultEmptyMessage, R.string.searchResultEmptyTitle, mFilter, mSortOrder, false);
    }

    public void setSortOrder(ActivitiesListView.Sorter sortOrder) {
        mSortOrder = sortOrder;

        //TODO: Really assume that first child is the one of interest?
        ActivitiesListView activitiesListView = (ActivitiesListView) ((ViewGroup) getView()).getChildAt(0);
        activitiesListView.setSortOrder(sortOrder);
    }

    public ActivitiesListView.Sorter getSortOrder() {
        return mSortOrder;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
         * Store fields into saved state, for example when the activity is destroyed after the device has been rotated.
         */
        Log.d(ActivitiesListFragment.class.getName(), "Saving state");
        outState.putSerializable("mSortOrder", mSortOrder);
        Log.d(ActivitiesListFragment.class.getName(), "State saved");
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

    public static ActivitiesListFragment create(ActivityFilter filter, ActivitiesListView.Sorter defaultSortOrder) {
        return new ActivitiesListFragment(filter, defaultSortOrder);
    }
}
