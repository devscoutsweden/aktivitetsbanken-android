package se.devscout.android.model;

import se.devscout.server.api.model.Category;

import java.io.Serializable;
import java.util.UUID;

public class LocalCategory extends LocalObjectIdentifier implements Category, Serializable {
    public static int debugCounter;
    private String mName;
    private String mGroup;
    private UUID mUUID;

    public LocalCategory() {
    }

    public LocalCategory(String group, String name, Integer id) {
        super(id);
        mGroup = group;
        mName = name;
        mUUID = UUID.randomUUID();
    }

/*
    @Override
    public CategoryKey getParentKey() {
        throw new UnsupportedOperationException();
    }
*/

    @Override
    public String getName() {
        return mName;
    }

/*
    @Override
    public UUID getUniqueID() {
        return mUUID;
    }
*/

    @Override
    public String getGroup() {
        return mGroup;
    }
}
