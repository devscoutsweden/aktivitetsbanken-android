package se.devscout.android.model;

import se.devscout.server.api.model.MediaProperties;
import se.devscout.server.api.model.Status;

import java.net.URI;

public class MediaPropertiesPojo implements MediaProperties {
    private URI mUri;
    private String mMimeType;

    public MediaPropertiesPojo() {
    }

    public MediaPropertiesPojo(URI uri, String mimeType) {
        mUri = uri;
        mMimeType = mimeType;
    }

    @Override
    public Status getStatus() {
        throw new UnsupportedOperationException();
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
