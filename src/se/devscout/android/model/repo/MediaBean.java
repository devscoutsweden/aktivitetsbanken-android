package se.devscout.android.model.repo;

import se.devscout.android.model.MediaPropertiesBean;
import se.devscout.server.api.model.Media;

import java.io.Serializable;
import java.net.URI;

public class MediaBean extends MediaPropertiesBean implements Media, Serializable {
    public static long debugCounter;
    private Long mId;

    public MediaBean(URI uri, String mimeType, Long id, long serverId, long serverRevisionId, boolean publishable) {
        super(uri, mimeType, serverId,serverRevisionId, publishable);
        mId = id;
    }

    @Override
    public Long getId() {
        return mId;
    }
}
