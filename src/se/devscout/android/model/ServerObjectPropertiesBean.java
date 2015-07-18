package se.devscout.android.model;

public class ServerObjectPropertiesBean extends ServerObjectIdentifierBean implements ServerObjectProperties {
    protected boolean mPublishable = false;
    private long mServerRevisionId;

    public ServerObjectPropertiesBean() {
    }

    public ServerObjectPropertiesBean(boolean publishable, long serverId, long serverRevisionId) {
        super(serverId);
        mPublishable = publishable;
        mServerRevisionId = serverRevisionId;
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

}
