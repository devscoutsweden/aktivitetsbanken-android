package se.devscout.android.model;

import java.net.URI;

public class ReferenceBean extends ReferencePropertiesBean implements Reference {
    public static long debugCounter;
    private Long mId;

    public ReferenceBean(Long id, long serverId, long serverRevisionId, URI uri, String description) {
        super(false, serverId, serverRevisionId, uri, description);
        mId = id;
    }

    @Override
    public Long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }
}
