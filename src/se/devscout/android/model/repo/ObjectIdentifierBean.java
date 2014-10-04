package se.devscout.android.model.repo;

import se.devscout.server.api.model.ObjectIdentifier;

import java.io.Serializable;

class ObjectIdentifierBean implements ObjectIdentifier, Serializable {
    private Long mId;

    public ObjectIdentifierBean() {
    }

    public ObjectIdentifierBean(Long id) {
        mId = id;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }
}
