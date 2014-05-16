package se.devscout.android.controller.fragment;

import android.widget.ArrayAdapter;
import se.devscout.android.util.SimpleIsFeaturedFilter;
import se.devscout.server.api.model.Activity;

import java.util.List;

public class FeaturedActivitiesListFragment extends ActivitiesListFragment {

    /**
     * No-args constructor necessary when support library restored fragment.
     */
    public FeaturedActivitiesListFragment() {
        this(Sorter.NAME);
    }

    public FeaturedActivitiesListFragment(Sorter sortOrder) {
        super(new SimpleIsFeaturedFilter(), sortOrder);
    }

    @Override
    protected ArrayAdapter<Activity> createAdapter(final List<Activity> result) {
        return new FeaturedActivitiesArrayAdapter(getActivity(), result);
    }

    public static FeaturedActivitiesListFragment create(Sorter defaultSortOrder) {
        return new FeaturedActivitiesListFragment(defaultSortOrder);
    }

}
