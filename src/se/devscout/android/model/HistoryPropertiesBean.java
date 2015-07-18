package se.devscout.android.model;

import java.io.Serializable;

public class HistoryPropertiesBean<D extends Serializable> implements HistoryProperties<D> {

    protected UserKey mUserKey;
    protected HistoryType mType;
    private D mData;

    public HistoryPropertiesBean(HistoryType type, UserKey userKey, D data) {
        mType = type;
        mUserKey = userKey;
        mData = data;
    }

    @Override
    public UserKey getUser() {
        return mUserKey;
    }

    @Override
    public HistoryType getType() {
        return mType;
    }

    @Override
    public D getData() {
        return mData;
    }
}
