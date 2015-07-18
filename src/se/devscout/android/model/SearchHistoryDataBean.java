package se.devscout.android.model;

import se.devscout.server.api.activityfilter.ActivityFilter;

public class SearchHistoryDataBean implements SearchHistoryData {
    private ActivityFilter mFilter;

    public SearchHistoryDataBean(ActivityFilter filter) {
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
