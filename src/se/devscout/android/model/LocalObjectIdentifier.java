package se.devscout.android.model;

import se.devscout.server.api.model.ObjectIdentifier;

import java.io.Serializable;

public class LocalObjectIdentifier implements ObjectIdentifier, Serializable {
    private Integer mId;

    public LocalObjectIdentifier() {
    }

    public LocalObjectIdentifier(Integer id) {
        mId = id;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }
}
