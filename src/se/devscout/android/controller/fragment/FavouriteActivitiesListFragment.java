package se.devscout.android.controller.fragment;

import android.util.Log;
import android.widget.ArrayAdapter;
import se.devscout.android.R;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.ActivityFilterFactoryException;
import se.devscout.server.api.model.Activity;

import java.util.Collections;
import java.util.List;

public class FavouriteActivitiesListFragment extends ActivitiesListFragment {

    /**
     * No-args constructor necessary when support library restored fragment.
     */
    public FavouriteActivitiesListFragment() {
        this(Sorter.NAME);
    }

    public FavouriteActivitiesListFragment(Sorter sortOrder) {
        super(R.string.favouritesEmptyHeader, R.string.favouritesEmptyMessage, null, sortOrder);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshResultList(true);
    }

    @Override
    protected List<Activity> doSearch() {
        ActivityBank activityBank = ActivityBankFactory.getInstance(getActivity());
        try {
            return (List<Activity>) activityBank.find(activityBank.getFilterFactory().createIsUserFavouriteFilter(null));
        } catch (ActivityFilterFactoryException e) {
            Log.e(FavouriteActivitiesListFragment.class.getName(), "Could not create filter.", e);
            return Collections.emptyList();
        }
    }

    @Override
    protected ArrayAdapter<Activity> createAdapter(final List<Activity> result) {
        return new FeaturedActivitiesArrayAdapter(getActivity(), result);
    }

    public static FavouriteActivitiesListFragment create() {
        return new FavouriteActivitiesListFragment();
    }

}
