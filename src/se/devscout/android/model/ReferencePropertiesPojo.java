package se.devscout.android.model;

import se.devscout.server.api.model.ReferenceProperties;
import se.devscout.server.api.model.ReferenceType;

import java.net.URI;

public class ReferencePropertiesPojo implements ReferenceProperties {
    private URI mURI;
    private ReferenceType mType;

    public ReferencePropertiesPojo(ReferenceType type, URI URI) {
        mType = type;
        mURI = URI;
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
