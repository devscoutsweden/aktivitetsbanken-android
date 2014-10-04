package se.devscout.android.model;

import se.devscout.server.api.model.User;

public class UserBean extends UserPropertiesBean implements User {
    public UserBean(String displayName, String emailAddress, Long id, long serverId, long serverRevisionId) {
        super(displayName, emailAddress, serverId, serverRevisionId, false);
        mId = id;
    }

    private Long mId;

    @Override
    public Long getId() {
        return mId;
    }
}
