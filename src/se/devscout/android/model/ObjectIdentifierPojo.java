package se.devscout.android.model;

import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.CategoryKey;

import java.io.Serializable;

public class ObjectIdentifierPojo implements Serializable, ActivityKey, CategoryKey {
    private Long mId;

    public ObjectIdentifierPojo(Long id) {
        mId = id;
    }

    @Override
    public Long getId() {
        return mId;
    }
}
