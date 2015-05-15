package se.devscout.android.model.repo.sql;

import android.database.sqlite.SQLiteDatabase;
import se.devscout.android.model.ActivityHistoryBean;
import se.devscout.server.api.model.ActivityHistoryData;
import se.devscout.server.api.model.HistoryType;
import se.devscout.server.api.model.UserKey;

public class ActivityHistoryCursor extends AbstractHistoryCursor<ActivityHistoryBean, ActivityHistoryData> {
    public ActivityHistoryCursor(SQLiteDatabase db, UserKey user, boolean descendingOrder) {
        super(db, user, descendingOrder, HistoryType.ACTIVITY);
    }

    @Override
    protected ActivityHistoryBean createHistoryItem(long id, UserKey userKey, ActivityHistoryData data) {
        return new ActivityHistoryBean(id, userKey, data);
    }
}
