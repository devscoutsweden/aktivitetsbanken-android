package se.devscout.android.model;

import se.devscout.server.api.model.ReferenceProperties;
import se.devscout.server.api.model.ReferenceType;

import java.net.URI;

public class ReferencePropertiesPojo extends ServerObjectPropertiesPojo implements ReferenceProperties {
    private URI mURI;
    private ReferenceType mType;

    public ReferencePropertiesPojo(ReferenceType type, URI uri) {
        mType = type;
        mURI = uri;
    }

    public ReferencePropertiesPojo(boolean publishable, long serverId, long serverRevisionId, ReferenceType type, URI uri) {
        super(publishable, serverId, serverRevisionId);
        mType = type;
        mURI = uri;
    }

    @Override
    public URI getURI() {
        return mURI;
    }

    @Override
    public ReferenceType getType() {
        return mType;
    }
    public void setURI(URI URI) {
        mURI = URI;
    }

    public void setType(ReferenceType type) {
        mType = type;
    }
}
