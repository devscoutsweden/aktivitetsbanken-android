package se.devscout.android.model;

import se.devscout.shared.data.model.Category;
import se.devscout.shared.data.model.CategoryKey;

import java.io.Serializable;
import java.util.UUID;

public class LocalCategory implements Category, Serializable {
    private String mGroup;
    private String mName;

    public LocalCategory(String group, String name) {
        mGroup = group;
        mName = name;
    }

    @Override
    public CategoryKey getParentKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public UUID getUniqueID() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getGroup() {
        return mGroup;
    }

    @Override
    public Integer getId() {
        throw new UnsupportedOperationException();
    }
}
