package se.devscout.android.model;

public class ServerActivityBean extends ActivityBean implements ServerActivity {
    private Double mMyRating;

    public ServerActivityBean(UserKey owner, Long id, long serverId, long serverRevisionId, boolean publishable) {
        super(owner, id, serverId, serverRevisionId, publishable);
    }

    @Override
    public Double getMyRating() {
        return mMyRating;
    }

    public void setMyRating(Double myRating) {
        mMyRating = myRating;
    }
}
