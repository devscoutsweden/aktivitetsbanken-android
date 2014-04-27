package se.devscout.android.controller.fragment;

import se.devscout.server.api.model.ActivityKey;

import java.io.Serializable;

public class KeyPojo implements Serializable, ActivityKey {
    private Long mId;

    public KeyPojo(Long id) {
        mId = id;
    }

    @Override
    public Long getId() {
        return mId;
    }
}
