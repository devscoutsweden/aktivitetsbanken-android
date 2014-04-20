package se.devscout.android.model;

import se.devscout.shared.data.model.Media;
import se.devscout.shared.data.model.Status;

import java.io.Serializable;
import java.net.URI;

public class LocalMedia implements Media, Serializable {
    private Integer mId = -1;
    private URI mUri;

    public LocalMedia(URI uri) {
        mUri = uri;
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
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getId() {
        return mId;
    }
}
