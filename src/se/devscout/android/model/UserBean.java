package se.devscout.android.model;

import se.devscout.server.api.model.User;

public class UserBean extends UserPropertiesBean implements User {
    public UserBean(String displayName, String apiKey, Long id, long serverId, long serverRevisionId, String emailAddress) {
        super(displayName, apiKey, serverId, serverRevisionId, false, emailAddress);
        mId = id;
    }

    private Long mId;

    @Override
    public Long getId() {
        return mId;
    }
}
