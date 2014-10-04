package se.devscout.android.model.repo;

import se.devscout.android.model.ReferencePropertiesPojo;
import se.devscout.server.api.model.Reference;
import se.devscout.server.api.model.ReferenceType;

import java.net.URI;

public class LocalReference extends ReferencePropertiesPojo implements Reference {
    public static long debugCounter;
    private Long mId;

    public LocalReference(Long id, long serverId, long serverRevisionId, ReferenceType type, URI uri) {
        super(false, serverId, serverRevisionId, type, uri);
        mId = id;
    }

    @Override
    public Long getId() {
        return mId;
    }
}
