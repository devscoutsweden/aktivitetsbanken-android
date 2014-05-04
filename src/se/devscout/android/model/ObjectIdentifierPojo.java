package se.devscout.android.model;

import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.CategoryKey;
import se.devscout.server.api.model.UserKey;

import java.io.Serializable;

public class ObjectIdentifierPojo implements Serializable, ActivityKey, CategoryKey, UserKey {
    private Long mId;

    public ObjectIdentifierPojo(Long id) {
        mId = id;
    }

    public ObjectIdentifierPojo(int id) {
        mId = Long.valueOf(id);
    }

    @Override
    public Long getId() {
        return mId;
    }
}
