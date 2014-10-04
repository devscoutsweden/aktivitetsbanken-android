package se.devscout.android.model;

import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.CategoryKey;
import se.devscout.server.api.model.UserKey;

import java.io.Serializable;

public class ObjectIdentifierBean implements Serializable, ActivityKey, CategoryKey, UserKey {
    private Long mId;

    public ObjectIdentifierBean(Long id) {
        mId = id;
    }

    public ObjectIdentifierBean(int id) {
        mId = Long.valueOf(id);
    }

    @Override
    public Long getId() {
        return mId;
    }
}
