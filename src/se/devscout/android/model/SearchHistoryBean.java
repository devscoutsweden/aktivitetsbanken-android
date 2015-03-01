package se.devscout.android.model;

import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.SearchHistory;
import se.devscout.server.api.model.SearchHistoryData;
import se.devscout.server.api.model.UserKey;

public class SearchHistoryBean extends SearchHistoryPropertiesBean implements SearchHistory {

    private static final EqualsFilterVisitor EQUALS_FILTER_VISITOR = new EqualsFilterVisitor();

    public SearchHistoryBean(Long id, UserKey userKey, SearchHistoryData data) {
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

        SearchHistoryBean that = (SearchHistoryBean) o;

        ActivityFilter thisDataFilter = getData().getFilter();
        ActivityFilter thatDataFilter = that.getData().getFilter();
        if (thisDataFilter != null ? !thisDataFilter.visit(EQUALS_FILTER_VISITOR).equals(thatDataFilter.visit(EQUALS_FILTER_VISITOR)) : thatDataFilter != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        SearchHistoryData data = getData();
        return data != null && data.getFilter() != null ? data.getFilter().visit(EQUALS_FILTER_VISITOR).hashCode() : 0;
    }

}
