package se.devscout.android.model.repo.sql;

import android.database.sqlite.SQLiteDatabase;
import se.devscout.android.model.ActivityHistoryBean;
import se.devscout.android.model.ActivityHistoryData;
import se.devscout.android.model.HistoryType;
import se.devscout.android.model.UserKey;

public class ActivityHistoryCursor extends AbstractHistoryCursor<ActivityHistoryBean, ActivityHistoryData> {
    public ActivityHistoryCursor(SQLiteDatabase db, UserKey user, boolean descendingOrder) {
        super(db, user, descendingOrder, HistoryType.ACTIVITY);
    }

    @Override
    protected ActivityHistoryBean createHistoryItem(long id, UserKey userKey, ActivityHistoryData data) {
        return new ActivityHistoryBean(id, userKey, data);
    }
}
