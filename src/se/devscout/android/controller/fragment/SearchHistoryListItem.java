package se.devscout.android.controller.fragment;

import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.SearchHistory;

import java.io.Serializable;

public class SearchHistoryListItem implements Serializable {
    private ActivityFilter mFilter;

    public SearchHistoryListItem(SearchHistory searchHistory) {
        mFilter = searchHistory.getData().getFilter();
    }

    public ActivityFilter getFilter() {
        return mFilter;
    }

    public void setFilter(ActivityFilter filter) {
        mFilter = filter;
    }
}
