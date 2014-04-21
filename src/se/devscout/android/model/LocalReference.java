package se.devscout.android.model;

import se.devscout.server.api.model.Reference;
import se.devscout.server.api.model.ReferenceType;

import java.io.Serializable;
import java.net.URI;

public class LocalReference extends LocalObjectIdentifier implements Reference, Serializable {
    public static int debugCounter;
    private URI mURI;
    private ReferenceType mType;

    public LocalReference() {
    }

    public LocalReference(Integer id, ReferenceType type, URI URI) {
        super(id);
        mType = type;
        mURI = URI;
    }

    public LocalReference(Integer id) {
        super(id);
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
