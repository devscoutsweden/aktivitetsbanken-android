package se.devscout.android.model;

import se.devscout.server.api.model.HistoryType;
import se.devscout.server.api.model.SearchHistoryData;
import se.devscout.server.api.model.UserKey;

public class SearchHistoryPropertiesBean extends HistoryPropertiesBean<SearchHistoryData> {

    public SearchHistoryPropertiesBean(UserKey userKey, SearchHistoryData data) {
        super(HistoryType.SEARCH, userKey, data);
    }
}
