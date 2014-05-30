package se.devscout.android.model;

import se.devscout.server.api.model.HistoryType;
import se.devscout.server.api.model.SearchHistoryData;
import se.devscout.server.api.model.UserKey;

public class SearchHistoryPropertiesPojo extends HistoryPropertiesPojo<SearchHistoryData> {

    public SearchHistoryPropertiesPojo(UserKey userKey, SearchHistoryData data) {
        super(HistoryType.SEARCH, userKey, data);
    }
}
