package se.devscout.android.controller.fragment;

import se.devscout.android.model.ActivityKey;
import se.devscout.android.model.SearchHistory;
import se.devscout.android.model.UserKey;
import se.devscout.server.api.ActivityBankListener;

public abstract class AbstractActivityBankListener implements ActivityBankListener {
    @Override
    public void onSearchHistoryItemAdded(SearchHistory searchHistory) {
    }

    @Override
    public void onFavouriteChange(ActivityKey activityKey, UserKey userKey, boolean isFavouriteNow) {
    }

    @Override
    public void onServiceDegradation(String message, Exception e) {
    }
}
