package se.devscout.android.model.repo;

import se.devscout.android.model.UserPropertiesPojo;
import se.devscout.server.api.model.User;

public class LocalUser extends UserPropertiesPojo implements User {
    public LocalUser(String displayName, boolean isLocalUser, String emailAddress, Long id) {
        super(displayName, isLocalUser, emailAddress);
        mId = id;
    }

    private Long mId;

    @Override
    public Long getId() {
        return mId;
    }
}
