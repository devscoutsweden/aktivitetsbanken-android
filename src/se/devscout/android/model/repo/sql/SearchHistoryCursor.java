package se.devscout.android.model.repo.sql;

import android.database.sqlite.SQLiteDatabase;
import se.devscout.android.model.SearchHistoryBean;
import se.devscout.server.api.model.HistoryType;
import se.devscout.server.api.model.SearchHistoryData;
import se.devscout.server.api.model.UserKey;

public class SearchHistoryCursor extends AbstractHistoryCursor<SearchHistoryBean, SearchHistoryData> {
    public SearchHistoryCursor(SQLiteDatabase db, UserKey user, boolean descendingOrder) {
        super(db.query(Database.history.T,
                new String[]{
                        Database.history.id,
                        Database.history.user_id,
                        Database.history.type,
                        Database.history.data
                },
                Database.history.user_id + " = " + user.getId() + " and " + Database.history.type + " = ?",
                new String[]{String.valueOf(HistoryType.SEARCH.getDatabaseValue())},
                null,
                null,
                Database.history.id + (descendingOrder ? " DESC" : "")));
    }

    @Override
    protected SearchHistoryBean createHistoryItem(long id, UserKey userKey, SearchHistoryData data) {
        return new SearchHistoryBean(id, userKey, data);
    }
}
