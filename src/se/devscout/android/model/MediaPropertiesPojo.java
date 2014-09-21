package se.devscout.android.model;

import se.devscout.server.api.model.MediaProperties;

import java.net.URI;

public class MediaPropertiesPojo extends ServerObjectPropertiesPojo implements MediaProperties {
    private URI mUri;
    private String mMimeType = "image/jpeg";
    private boolean publishable;
    private int serverId;

    public MediaPropertiesPojo() {
    }

    public MediaPropertiesPojo(URI uri, String mimeType, int serverId, boolean publishable) {
        super(publishable, serverId);
        mUri = uri;
        mMimeType = mimeType;
    }

    @Override
    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    @Override
    public boolean isPublishable() {
        return publishable;
    }

    public void setPublishable(boolean publishable) {
        this.publishable = publishable;
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
