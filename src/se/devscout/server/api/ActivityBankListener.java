package se.devscout.server.api;

import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.SearchHistory;
import se.devscout.server.api.model.UserKey;

public interface ActivityBankListener {
    void onSearchHistoryItemAdded(SearchHistory searchHistory);
    void onFavouriteChange(ActivityKey activityKey, UserKey userKey);
}
