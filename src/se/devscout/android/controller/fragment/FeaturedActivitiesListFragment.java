package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import se.devscout.android.view.ActivitiesListView;
import se.devscout.android.view.FeaturedActivitiesListView;
import se.devscout.server.api.activityfilter.IsFeaturedFilter;

public class FeaturedActivitiesListFragment extends ActivitiesListFragment {

    /**
     * No-args constructor necessary when support library restored fragment.
     */
    public FeaturedActivitiesListFragment() {
        this(ActivitiesListView.Sorter.NAME, null);
    }

    public FeaturedActivitiesListFragment(ActivitiesListView.Sorter sortOrder, IsFeaturedFilter featuredFilter) {
        super(featuredFilter, sortOrder);
    }

    @Override
    protected ActivitiesListView createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new FeaturedActivitiesListView(this, getSortOrder(), false);
    }

    public static FeaturedActivitiesListFragment create() {
        return new FeaturedActivitiesListFragment();
    }

}
