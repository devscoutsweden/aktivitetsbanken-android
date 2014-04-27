package se.devscout.android.model.repo;

import se.devscout.android.model.UserPropertiesPojo;
import se.devscout.server.api.model.User;

class LocalUser extends UserPropertiesPojo implements User {

    private Long mId;

    @Override
    public Long getId() {
        return mId;
    }
}
