package se.devscout.android.model;

import com.google.gson.annotations.SerializedName;
import se.devscout.server.api.model.Category;
import se.devscout.server.api.model.CategoryKey;

import java.io.Serializable;
import java.util.UUID;

public class LocalCategory implements Category, Serializable {
    @SerializedName("name")
    private String mName;
    @SerializedName("group")
    private String mGroup;

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
