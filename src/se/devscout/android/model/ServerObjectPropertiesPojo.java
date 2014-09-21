package se.devscout.android.model;

public class ServerObjectPropertiesPojo {
    protected int mServerId;
    protected boolean mPublishable = false;

    public ServerObjectPropertiesPojo() {
    }

    public ServerObjectPropertiesPojo(boolean publishable, int serverId) {
        mPublishable = publishable;
        mServerId = serverId;
    }

    public int getServerId() {
        return mServerId;
    }

    public boolean isPublishable() {
        return mPublishable;
    }

    public void setPublishable(boolean publishable) {
        mPublishable = publishable;
    }
}
