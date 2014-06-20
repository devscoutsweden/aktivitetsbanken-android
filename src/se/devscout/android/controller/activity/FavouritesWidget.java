package se.devscout.android.controller.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivitiesListFragment;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.view.FavouriteActivitiesListView;
import se.devscout.server.api.ActivityBankListener;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.SearchHistory;
import se.devscout.server.api.model.UserKey;

public class FavouritesWidget implements StartScreenWidget, ActivityBankListener {
    private boolean mRefreshResultOnResume = false;
    private FavouriteActivitiesListView mView;

    public FavouritesWidget() {
    }


    @Override
    public int getTitleResId() {
        return R.string.startTabFavourites;
    }

    @Override
    public View[] getViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = new FavouriteActivitiesListView(container.getContext(), true);

        // Start search in separate thread
        Log.d(ActivitiesListFragment.class.getName(), "Result has not been returned/cached. Starting search task in new thread.");

        mView.createSearchTask().execute();

        ActivityBankFactory.getInstance(container.getContext()).addListener(this);
        return new View[]{mView};
    }

    @Override
    public void onFragmentResume() {
        synchronized (this) {
            if (mRefreshResultOnResume) {
                mView.createSearchTask().execute();
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
}
