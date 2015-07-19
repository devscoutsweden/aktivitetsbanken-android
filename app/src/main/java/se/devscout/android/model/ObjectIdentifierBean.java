package se.devscout.android.model;

import java.io.Serializable;

public class ObjectIdentifierBean implements Serializable, ActivityKey, CategoryKey, UserKey, MediaKey {
    private Long mId;

    public ObjectIdentifierBean(Long id) {
        mId = id;
    }

    public ObjectIdentifierBean(ObjectIdentifier identifier) {
        this(identifier.getId());
    }

    @Override
    public Long getId() {
        return mId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectIdentifierBean)) return false;

        ObjectIdentifierBean that = (ObjectIdentifierBean) o;

        if (!mId.equals(that.mId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }

    @Override
    public String toString() {
        return String.valueOf(mId);
    }
}
