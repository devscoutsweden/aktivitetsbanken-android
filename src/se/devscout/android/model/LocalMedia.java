package se.devscout.android.model;

import se.devscout.server.api.model.Media;

import java.io.Serializable;
import java.net.URI;

class LocalMedia extends MediaPropertiesPojo implements Media, Serializable {
    public static long debugCounter;
    private Long mId;

    LocalMedia(URI uri, String mimeType, Long id) {
        super(uri, mimeType);
        mId = id;
    }

    @Override
    public Long getId() {
        return mId;
    }
}
