package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import se.devscout.android.view.ActivitiesListView;
import se.devscout.android.view.FeaturedActivitiesListView;

public class FeaturedActivitiesListFragment extends ActivitiesListFragment {

    /**
     * No-args constructor necessary when support library restored fragment.
     */
    public FeaturedActivitiesListFragment() {
    }

    @Override
    protected ActivitiesListView createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new FeaturedActivitiesListView(this, getSortOrder(), false);
    }

    public static FeaturedActivitiesListFragment create() {
        final FeaturedActivitiesListFragment fragment = new FeaturedActivitiesListFragment();
        fragment.setArguments(null, ActivitiesListView.Sorter.NAME, false);
        return fragment;
    }

}
