package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.view.ActivitiesListView;
import se.devscout.android.view.OverallFavouriteActivitiesListView;
public class OverallFavouriteActivitiesListFragment extends ActivitiesListFragment {

    private long mFavouriteListModificationCounter;

    /**
     * No-args constructor necessary when support library restored fragment.
     */
    public OverallFavouriteActivitiesListFragment() {
    }

    @Override
    protected ActivitiesListView createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new OverallFavouriteActivitiesListView(this, ActivitiesListView.Sorter.NAME, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mFavouriteListModificationCounter = savedInstanceState.getLong("mFavouriteListModificationCounter");
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong("mFavouriteListModificationCounter", mFavouriteListModificationCounter);
        super.onSaveInstanceState(outState);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onResume() {
        super.onResume();
        long latest = getActivityBank().getModificationCounters().getFavouriteList();
        if (mFavouriteListModificationCounter < latest || !getListView().isResultPresent()) {
            mFavouriteListModificationCounter = latest;
            getListView().runSearchTaskInNewThread();
        }
    }

    public static OverallFavouriteActivitiesListFragment create() {
        return new OverallFavouriteActivitiesListFragment();
    }
}
