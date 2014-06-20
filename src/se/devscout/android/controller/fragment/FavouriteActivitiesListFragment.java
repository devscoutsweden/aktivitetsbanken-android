package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.view.ActivitiesListView;
import se.devscout.android.view.FavouriteActivitiesListView;
import se.devscout.server.api.ActivityBankListener;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.SearchHistory;
import se.devscout.server.api.model.UserKey;

public class FavouriteActivitiesListFragment extends ActivitiesListFragment implements ActivityBankListener {

    private boolean mRefreshResultOnResume = false;

    /**
     * No-args constructor necessary when support library restored fragment.
     */
    public FavouriteActivitiesListFragment() {
//        this(ActivitiesListFragment.Sorter.NAME);
    }

//    public FavouriteActivitiesListFragment(ActivitiesListFragment.Sorter sortOrder) {
//        super(R.string.favouritesEmptyHeader, R.string.favouritesEmptyMessage, null, sortOrder);
//    }

    @Override
    protected ActivitiesListView createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new FavouriteActivitiesListView(getActivity(), ActivitiesListFragment.Sorter.NAME, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivityBank().addListener(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        synchronized (this) {
            if (mRefreshResultOnResume) {
                getListView().createSearchTask().execute();
            }
        }
    }

    @Override
    public void onSearchHistoryItemAdded(SearchHistory searchHistory) {
    }

    @Override
    public void onFavouriteChange(ActivityKey activityKey, UserKey userKey, boolean isFavouriteNow) {
        synchronized (this) {
            mRefreshResultOnResume = true;
        }
    }

    public static FavouriteActivitiesListFragment create() {
        return new FavouriteActivitiesListFragment();
    }
}
