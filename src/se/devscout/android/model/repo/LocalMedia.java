package se.devscout.android.model.repo;

import se.devscout.android.model.MediaPropertiesPojo;
import se.devscout.server.api.model.Media;

import java.io.Serializable;
import java.net.URI;

public class LocalMedia extends MediaPropertiesPojo implements Media, Serializable {
    public static long debugCounter;
    private Long mId;

    public LocalMedia(URI uri, String mimeType, Long id) {
        super(uri, mimeType);
        mId = id;
    }

    @Override
    public Long getId() {
        return mId;
    }
}
