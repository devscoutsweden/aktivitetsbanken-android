package se.devscout.android.model.repo.sql.cache;

import se.devscout.android.model.ReferenceBean;
import se.devscout.android.model.repo.sql.Database;
import se.devscout.android.model.repo.sql.DatabaseHelper;
import se.devscout.server.api.model.ReferenceProperties;

public class ReferenceIdCache extends ServerObjectIdCache<ReferenceBean, ReferenceProperties> {
    public ReferenceIdCache(DatabaseHelper databaseHelper) {
        super(databaseHelper, Database.reference.T, Database.reference.id, Database.reference.server_id, Database.reference.server_revision_id);
    }

    @Override
    protected IdCacheEntry createIdCacheEntry(ReferenceBean entry) {
        return new IdCacheEntry(entry.getId(), entry.getServerId(), new double[]{entry.getServerRevisionId()});
    }

    @Override
    protected ReferenceBean createObject(long id, ReferenceProperties properties) {
        return new ReferenceBean(id, properties.getServerId(), properties.getServerRevisionId(), properties.getURI(), properties.getDescription());
    }
}
