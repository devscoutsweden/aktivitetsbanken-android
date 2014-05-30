package se.devscout.android.model.repo;

import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.SearchHistoryData;

public class SearchHistoryDataPojo implements SearchHistoryData {
    private ActivityFilter mFilter;

    public SearchHistoryDataPojo(ActivityFilter filter) {
        mFilter = filter;
    }

    @Override
    public ActivityFilter getFilter() {
        return mFilter;
    }

    public void setFilter(ActivityFilter filter) {
        mFilter = filter;
    }

}
