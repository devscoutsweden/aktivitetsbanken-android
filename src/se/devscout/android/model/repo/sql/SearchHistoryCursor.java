package se.devscout.android.model.repo.sql;

import android.database.Cursor;
import se.devscout.android.model.repo.LocalSearchHistory;
import se.devscout.server.api.model.SearchHistoryData;
import se.devscout.server.api.model.UserKey;

public class SearchHistoryCursor extends AbstractHistoryCursor<LocalSearchHistory, SearchHistoryData> {
    public SearchHistoryCursor(Cursor cursor) {
        super(cursor);
    }

    @Override
    protected LocalSearchHistory createHistoryItem(long id, UserKey userKey, SearchHistoryData data) {
        return new LocalSearchHistory(id, userKey, data);
    }
}
