package se.devscout.android.model;

import se.devscout.server.api.model.ReferenceProperties;

import java.net.URI;

public class ReferencePropertiesBean extends ServerObjectPropertiesBean implements ReferenceProperties {
    private URI mURI;
    private String mDescription;

    public ReferencePropertiesBean(URI uri, String description) {
        mURI = uri;
        mDescription = description;
    }

    public ReferencePropertiesBean(boolean publishable, long serverId, long serverRevisionId, URI uri, String description) {
        super(publishable, serverId, serverRevisionId);
        mURI = uri;
        mDescription = description;
    }

    @Override
    public URI getURI() {
        return mURI;
    }

    @Override
    public String getDescription() {
        return mDescription;
    }

    public void setURI(URI URI) {
        mURI = URI;
    }
}
