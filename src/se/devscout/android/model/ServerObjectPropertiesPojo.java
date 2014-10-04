package se.devscout.android.model;

import se.devscout.server.api.model.ServerObjectProperties;

public class ServerObjectPropertiesPojo implements ServerObjectProperties {
    protected long mServerId;
    protected boolean mPublishable = false;
    private long mServerRevisionId;

    public ServerObjectPropertiesPojo() {
    }

    public ServerObjectPropertiesPojo(boolean publishable, long serverId, long serverRevisionId) {
        mPublishable = publishable;
        mServerId = serverId;
        mServerRevisionId = serverRevisionId;
    }

    @Override
    public long getServerId() {
        return mServerId;
    }

    @Override
    public boolean isPublishable() {
        return mPublishable;
    }

    public void setPublishable(boolean publishable) {
        mPublishable = publishable;
    }

    @Override
    public long getServerRevisionId() {
        return mServerRevisionId;
    }

    public void setServerRevisionId(long serverRevisionId) {
        this.mServerRevisionId = serverRevisionId;
    }

    public void setServerId(long serverId) {
        mServerId = serverId;
    }
}
