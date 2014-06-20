package se.devscout.android.controller.fragment;

import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.activityfilter.IsFeaturedFilter;

public class FeaturedActivitiesListFragment extends ActivitiesListFragment {

    /**
     * No-args constructor necessary when support library restored fragment.
     */
    public FeaturedActivitiesListFragment() {
        this(Sorter.NAME, null);
    }

    public FeaturedActivitiesListFragment(Sorter sortOrder, IsFeaturedFilter featuredFilter) {
        super(featuredFilter, sortOrder);
    }

/*
    @Override
    protected ArrayAdapter<Activity> createAdapter(final List<Activity> result) {
        return new FeaturedActivitiesArrayAdapter(getActivity(), result);
    }
*/

    public static FeaturedActivitiesListFragment create(Sorter defaultSortOrder, ActivityBank activityBank) {
        return new FeaturedActivitiesListFragment(defaultSortOrder, activityBank.getFilterFactory().createIsFeaturedFilter());
    }

}
