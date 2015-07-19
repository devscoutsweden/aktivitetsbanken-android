package se.devscout.android.view;

import se.devscout.android.model.SearchHistory;
import se.devscout.android.model.activityfilter.ActivityFilter;

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
