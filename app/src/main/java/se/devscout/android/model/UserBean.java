package se.devscout.android.model;

public class UserBean extends UserPropertiesBean implements User {
    public UserBean(String displayName, String apiKey, Long id, long serverId, long serverRevisionId, String emailAddress, String role) {
        super(displayName, apiKey, serverId, serverRevisionId, false, emailAddress, role);
        mId = id;
    }

    private Long mId;

    @Override
    public Long getId() {
        return mId;
    }
}
