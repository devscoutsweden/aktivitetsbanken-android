package se.devscout.android.model;

import se.devscout.server.api.model.Media;
import se.devscout.server.api.model.Status;

import java.io.Serializable;
import java.net.URI;

public class LocalMedia extends LocalObjectIdentifier implements Media, Serializable {
    public static int debugCounter;
    private URI mUri;
    private String mMimeType;

    public LocalMedia() {
    }

    public LocalMedia(URI uri, String mimeType, Integer id) {
        super(id);
        mUri = uri;
        mMimeType = mimeType;
    }

    @Override
    public Status getStatus() {
        return Status.PUBLISHED;
    }

    @Override
    public URI getURI() {
        return mUri;
    }

    @Override
    public String getMimeType() {
        return mMimeType;
    }
}
