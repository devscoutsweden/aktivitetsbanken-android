package se.devscout.android.model;

import se.devscout.server.api.model.ServerObjectIdentifier;

import java.io.Serializable;

public class ServerObjectIdentifierBean implements ServerObjectIdentifier, Serializable {
    private long mServerId;

    public ServerObjectIdentifierBean() {
    }

    public ServerObjectIdentifierBean(long serverId) {
        mServerId = serverId;
    }

    public long getServerId() {
        return mServerId;
    }

    public void setServerId(long serverId) {
        mServerId = serverId;
    }

    @Override
    public String toString() {
        return String.valueOf(mServerId);
    }
}
