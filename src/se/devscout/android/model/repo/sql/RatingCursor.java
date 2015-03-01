package se.devscout.android.model.repo.sql;

import android.database.sqlite.SQLiteDatabase;
import se.devscout.android.model.RatingBean;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.RatingStatus;
import se.devscout.server.api.model.UserKey;

public class RatingCursor extends BaseCursorWrapper {
    public RatingCursor(SQLiteDatabase db, ActivityKey activityKey, UserKey userKey) {
        super(db.query(
                Database.rating.T,
                new String[]{Database.rating.activity_id, Database.rating.user_id, Database.rating.rating, Database.rating.status},
                activityKey != null && userKey != null ? Database.rating.activity_id + " = " + activityKey.getId() + " AND " + Database.rating.user_id + " = " + userKey.getId() :
                        activityKey != null ? Database.rating.activity_id + " = " + activityKey.getId() :
                                userKey != null ? Database.rating.user_id + " = " + userKey.getId() :
                                        null,
                null,
                null,
                null,
                null));
    }

    public RatingBean getRating() {
        return new RatingBean(
                getInt(getColumnIndex(Database.rating.rating)),
                RatingStatus.valueOf(getString(getColumnIndex(Database.rating.status))),
                getLong(getColumnIndex(Database.rating.activity_id)),
                getLong(getColumnIndex(Database.rating.user_id))
        );
    }
}
