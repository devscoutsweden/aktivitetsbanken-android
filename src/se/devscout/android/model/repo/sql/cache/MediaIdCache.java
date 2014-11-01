package se.devscout.android.model.repo.sql.cache;

import se.devscout.android.model.MediaBean;
import se.devscout.android.model.repo.sql.Database;
import se.devscout.android.model.repo.sql.DatabaseHelper;
import se.devscout.server.api.model.MediaProperties;

public class MediaIdCache extends ServerObjectIdCache<MediaBean, MediaProperties> {
    public MediaIdCache(DatabaseHelper databaseHelper) {
        super(databaseHelper, Database.media.T, Database.media.id, Database.media.server_id, Database.media.server_revision_id);
    }

    @Override
    protected IdCacheEntry createIdCacheEntry(MediaBean entry) {
        return new IdCacheEntry(entry.getId(), entry.getServerId(), new long[]{entry.getServerRevisionId()});
    }

    @Override
    protected MediaBean createObject(long id, MediaProperties properties) {
        return new MediaBean(properties.getURI(), properties.getMimeType(), id, properties.getServerId(), properties.getServerRevisionId(), properties.isPublishable());
    }
}
