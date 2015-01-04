package se.devscout.android.model.repo.sql.cache;

import se.devscout.android.model.CategoryBean;
import se.devscout.android.model.repo.sql.Database;
import se.devscout.android.model.repo.sql.DatabaseHelper;
import se.devscout.server.api.model.CategoryProperties;

public class CategoryIdCache extends ServerObjectIdCache<CategoryBean, CategoryProperties> {
    public CategoryIdCache(DatabaseHelper databaseHelper) {
        super(databaseHelper, Database.category.T, Database.category.id, Database.category.server_id, Database.category.server_revision_id, Database.category.activities_count);
    }

    @Override
    protected CategoryBean createObject(long id, CategoryProperties properties) {
        return new CategoryBean(properties.getGroup(), properties.getName(), id, properties.getServerId(), properties.getServerRevisionId(), properties.getIconMediaKey(), properties.getActivitiesCount());
    }

    @Override
    protected IdCacheEntry createIdCacheEntry(CategoryBean entry) {
        return new IdCacheEntry(entry.getId(), entry.getServerId(), new long[]{entry.getServerRevisionId(), entry.getActivitiesCount() != null ? entry.getActivitiesCount() : -1});
    }
}
