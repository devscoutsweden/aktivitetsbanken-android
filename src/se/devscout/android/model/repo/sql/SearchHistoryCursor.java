package se.devscout.android.model.repo.sql;

import android.database.sqlite.SQLiteDatabase;
import se.devscout.android.model.SearchHistoryBean;
import se.devscout.server.api.model.HistoryType;
import se.devscout.server.api.model.SearchHistoryData;
import se.devscout.server.api.model.UserKey;

public class SearchHistoryCursor extends AbstractHistoryCursor<SearchHistoryBean, SearchHistoryData> {
    public SearchHistoryCursor(SQLiteDatabase db, UserKey user, boolean descendingOrder) {
        super(db, user, descendingOrder, HistoryType.SEARCH);
    }

    @Override
    protected SearchHistoryBean createHistoryItem(long id, UserKey userKey, SearchHistoryData data) {
        return new SearchHistoryBean(id, userKey, data);
    }
}
