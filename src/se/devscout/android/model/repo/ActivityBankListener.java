package se.devscout.android.model.repo;

import se.devscout.android.model.ActivityKey;
import se.devscout.android.model.SearchHistory;
import se.devscout.android.model.UserKey;

public interface ActivityBankListener {
    void onSearchHistoryItemAdded(SearchHistory searchHistory);

    void onFavouriteChange(ActivityKey activityKey, UserKey userKey, boolean isFavouriteNow);

    void onServiceDegradation(String message, Exception e);
}
