package se.devscout.android.model.repo.sql.cache;

import se.devscout.android.model.ActivityBean;
import se.devscout.android.model.repo.sql.Database;
import se.devscout.android.model.repo.sql.DatabaseHelper;
import se.devscout.server.api.model.ActivityProperties;

public class ActivityIdCache extends ServerObjectIdCache<ActivityBean, ActivityProperties> {
    public ActivityIdCache(DatabaseHelper databaseHelper) {
        super(databaseHelper, Database.activity.T, Database.activity.id, Database.activity.server_id, Database.activity.server_revision_id, Database.activity.favourite_count);
    }

    @Override
    protected IdCacheEntry createIdCacheEntry(ActivityBean entry) {
        return new IdCacheEntry(entry.getId(), entry.getServerId(), new long[]{entry.getServerRevisionId(), entry.getFavouritesCount() != null ? entry.getFavouritesCount().longValue() : 0});
    }

    public void addEntry(long id, ActivityProperties properties) {
        ActivityBean activityBean = new ActivityBean(null, id, properties.getServerId(), properties.getServerRevisionId(), false);
        activityBean.setFavouritesCount(properties.getFavouritesCount());
        addEntry(activityBean);
    }

    @Override
    protected ActivityBean createObject(long id, ActivityProperties properties) {
        ActivityBean activityBean = new ActivityBean(null, id, properties.getServerId(), properties.getServerRevisionId(), false);
        activityBean.setFavouritesCount(properties.getFavouritesCount());
        return activityBean;
    }
}
