package se.devscout.android.view.widget;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivitiesListFragment;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.view.FavouriteActivitiesListView;

public class FavouritesWidgetSpecification implements WidgetSpecification/*, ActivityBankListener */{
//    private boolean mRefreshResultOnResume = false;
    private FavouriteActivitiesListView mView;

    public FavouritesWidgetSpecification() {
    }


    @Override
    public int getTitleResId() {
        return R.string.startTabFavourites;
    }

    @Override
    public View[] getViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, ActivityBankFragment activityBankFragment) {

        mView = new FavouriteActivitiesListView(activityBankFragment, true);

        // Start search in separate thread
        Log.d(ActivitiesListFragment.class.getName(), "Result has not been returned/cached. Starting search task in new thread.");

        mView.runSearchTaskInNewThread();

//        ActivityBankFactory.getInstance(container.getContext()).addListener(this);
        return new View[]{mView};
    }

/*
    @Override
    public void onSearchHistoryItemAdded(SearchHistory searchHistory) {
    }

    @Override
    public void onFavouriteChange(ActivityKey activityKey, UserKey userKey, boolean isFavouriteNow) {
        synchronized (this) {
            mRefreshResultOnResume = true;
        }
    }
*/
}
