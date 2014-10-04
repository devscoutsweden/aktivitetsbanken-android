package se.devscout.android.model;

import se.devscout.server.api.model.MediaProperties;

import java.net.URI;

public class MediaPropertiesPojo extends ServerObjectPropertiesPojo implements MediaProperties {
    private URI mUri;
    private String mMimeType = "image/jpeg";

    public MediaPropertiesPojo() {
    }

    public MediaPropertiesPojo(URI uri, String mimeType, long serverId, long serverRevisionId, boolean publishable) {
        super(publishable, serverId, serverRevisionId);
        mUri = uri;
        mMimeType = mimeType;
    }

    @Override
    public URI getURI() {
        return mUri;
    }

    @Override
    public String getMimeType() {
        return mMimeType;
    }

    void setMimeType(String mimeType) {
        mMimeType = mimeType;
    }

    void setUri(URI uri) {
        mUri = uri;
    }
}
