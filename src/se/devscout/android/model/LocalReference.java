package se.devscout.android.model;

import se.devscout.server.api.model.Reference;
import se.devscout.server.api.model.ReferenceType;

import java.net.URI;

class LocalReference extends ReferencePropertiesPojo implements Reference {
    public static long debugCounter;
    private Long mId;

    public LocalReference(Long id, ReferenceType type, URI uri) {
        super(type, uri);
        mId = id;
    }

    @Override
    public Long getId() {
        return mId;
    }
}
