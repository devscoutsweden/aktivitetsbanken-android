package se.devscout.android.model.repo;

import se.devscout.server.api.model.ObjectIdentifier;

import java.io.Serializable;

class LocalObjectIdentifier implements ObjectIdentifier, Serializable {
    private Long mId;

    public LocalObjectIdentifier() {
    }

    public LocalObjectIdentifier(Long id) {
        mId = id;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }
}
