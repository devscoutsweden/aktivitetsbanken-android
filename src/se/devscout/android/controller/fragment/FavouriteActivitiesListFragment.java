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
//TODO: It might be cleaner to create an AbstractActivityBankListener instead of implementing ActivityBankListener
public class FavouriteActivitiesListFragment extends ActivitiesListFragment implements ActivityBankListener {

    private boolean mRefreshResultOnResume = false;

    /**
     * No-args constructor necessary when support library restored fragment.
     */
    public FavouriteActivitiesListFragment() {
    }

    @Override
    protected ActivitiesListView createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new FavouriteActivitiesListView(this, ActivitiesListView.Sorter.NAME, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mRefreshResultOnResume = savedInstanceState.getBoolean("mRefreshResultOnResume");
        }
        getActivityBank().addListener(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("mRefreshResultOnResume", mRefreshResultOnResume);
        super.onSaveInstanceState(outState);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onDestroyView() {
        getActivityBank().removeListener(this);
        super.onDestroyView();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mRefreshResultOnResume || !getListView().isResultPresent()) {
            //TODO: Necessary? Remove?
            getListView().runSearchTaskInNewThread();
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

    @Override
    public void onServiceDegradation(String message, Exception e) {
    }

    public static FavouriteActivitiesListFragment create() {
        return new FavouriteActivitiesListFragment();
    }
}
