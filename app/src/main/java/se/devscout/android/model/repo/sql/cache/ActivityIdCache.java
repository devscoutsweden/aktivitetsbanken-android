package se.devscout.android.model.repo.sql.cache;

import se.devscout.android.model.ActivityBean;
import se.devscout.android.model.ActivityProperties;
import se.devscout.android.model.repo.sql.Database;
import se.devscout.android.model.repo.sql.DatabaseHelper;

public class ActivityIdCache extends ServerObjectIdCache<ActivityBean, ActivityProperties> {
    public ActivityIdCache(DatabaseHelper databaseHelper) {
        super(databaseHelper, Database.activity.T, Database.activity.id, Database.activity.server_id, Database.activity.server_revision_id, Database.activity.favourite_count, Database.activity.rating_average);
    }

    @Override
    protected IdCacheEntry createIdCacheEntry(ActivityBean entry) {
        return new IdCacheEntry(
                entry.getId(),
                entry.getServerId(),
                new double[]{
                        entry.getServerRevisionId(),
                        entry.getFavouritesCount() != null ? entry.getFavouritesCount().doubleValue() : 0,
                        entry.getRatingAverage() != null ? entry.getRatingAverage().doubleValue() : 0});
    }

    public void addEntry(long id, ActivityProperties properties) {
        ActivityBean activityBean = new ActivityBean(null, id, properties.getServerId(), properties.getServerRevisionId(), false);
        activityBean.setFavouritesCount(properties.getFavouritesCount());
        activityBean.setRatingAverage(properties.getRatingAverage());
        addEntry(activityBean);
    }

    @Override
    protected ActivityBean createObject(long id, ActivityProperties properties) {
        ActivityBean activityBean = new ActivityBean(null, id, properties.getServerId(), properties.getServerRevisionId(), false);
        activityBean.setFavouritesCount(properties.getFavouritesCount());
        activityBean.setRatingAverage(properties.getRatingAverage());
        return activityBean;
    }
}
