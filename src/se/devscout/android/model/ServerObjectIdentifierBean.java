package se.devscout.android.model;

import se.devscout.server.api.model.ServerObjectIdentifier;

public class ServerObjectIdentifierBean implements ServerObjectIdentifier {
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
