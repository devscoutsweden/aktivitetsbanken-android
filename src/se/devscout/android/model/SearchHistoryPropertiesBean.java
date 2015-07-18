package se.devscout.android.model;

public class SearchHistoryPropertiesBean extends HistoryPropertiesBean<SearchHistoryData> {

    public SearchHistoryPropertiesBean(UserKey userKey, SearchHistoryData data) {
        super(HistoryType.SEARCH, userKey, data);
    }
}
