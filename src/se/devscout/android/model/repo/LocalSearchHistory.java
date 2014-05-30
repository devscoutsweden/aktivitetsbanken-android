package se.devscout.android.model.repo;

import se.devscout.android.model.SearchHistoryPropertiesPojo;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.SearchHistory;
import se.devscout.server.api.model.SearchHistoryData;
import se.devscout.server.api.model.UserKey;

public class LocalSearchHistory extends SearchHistoryPropertiesPojo implements SearchHistory {

    private static final EqualsFilterVisitor EQUALS_FILTER_VISITOR = new EqualsFilterVisitor();

    public LocalSearchHistory(Long id, UserKey userKey, SearchHistoryData data) {
        super(userKey, data);
        mId = id;
    }

    private Long mId;

    @Override
    public Long getId() {
        return mId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalSearchHistory that = (LocalSearchHistory) o;

        ActivityFilter thisDataFilter = getData().getFilter();
        ActivityFilter thatDataFilter = that.getData().getFilter();
        if (thisDataFilter != null ? !thisDataFilter.toString(EQUALS_FILTER_VISITOR).equals(thatDataFilter.toString(EQUALS_FILTER_VISITOR)) : thatDataFilter != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        SearchHistoryData data = getData();
        return data != null && data.getFilter() != null ? data.getFilter().toString(EQUALS_FILTER_VISITOR).hashCode() : 0;
    }

}
