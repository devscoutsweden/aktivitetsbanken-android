package se.devscout.android.model;

import se.devscout.server.api.model.HistoryProperties;
import se.devscout.server.api.model.HistoryType;
import se.devscout.server.api.model.UserKey;

import java.io.Serializable;

public class HistoryPropertiesPojo<D extends Serializable> implements HistoryProperties<D> {

    protected UserKey mUserKey;
    protected HistoryType mType;
    private D mData;

    public HistoryPropertiesPojo(HistoryType type, UserKey userKey, D data) {
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