package se.devscout.android.controller.fragment;

import se.devscout.android.view.ActivitiesListView;
import se.devscout.server.api.ActivityBank;
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

    public static FeaturedActivitiesListFragment create(ActivitiesListView.Sorter defaultSortOrder, ActivityBank activityBank) {
        return new FeaturedActivitiesListFragment(defaultSortOrder, activityBank.getFilterFactory().createIsFeaturedFilter());
    }

}
