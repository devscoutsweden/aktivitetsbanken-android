package se.devscout.android.model.repo.sql;

import android.database.Cursor;
import se.devscout.android.model.repo.SearchHistoryBean;
import se.devscout.server.api.model.SearchHistoryData;
import se.devscout.server.api.model.UserKey;

public class SearchHistoryCursor extends AbstractHistoryCursor<SearchHistoryBean, SearchHistoryData> {
    public SearchHistoryCursor(Cursor cursor) {
        super(cursor);
    }

    @Override
    protected SearchHistoryBean createHistoryItem(long id, UserKey userKey, SearchHistoryData data) {
        return new SearchHistoryBean(id, userKey, data);
    }
}
