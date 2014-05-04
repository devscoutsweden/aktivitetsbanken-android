package se.devscout.android.controller.fragment;

import android.widget.ArrayAdapter;
import se.devscout.android.R;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.server.api.model.Activity;

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
        return (List<Activity>) ActivityBankFactory.getInstance(getActivity()).findFavourites(null);
    }

    @Override
    protected ArrayAdapter<Activity> createAdapter(final List<Activity> result) {
        return new FeaturedActivitiesArrayAdapter(getActivity(), result);
    }

    public static FavouriteActivitiesListFragment create() {
        return new FavouriteActivitiesListFragment();
    }

}
