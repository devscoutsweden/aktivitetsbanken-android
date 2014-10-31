package se.devscout.android.model;

import se.devscout.server.api.model.CategoryProperties;
import se.devscout.server.api.model.MediaKey;

import java.io.Serializable;

public class CategoryPropertiesBean extends ServerObjectPropertiesBean implements CategoryProperties, Serializable {
    private String mName;
    private String mGroup;
    private MediaKey mIconMediaKey;

    public CategoryPropertiesBean(String group, String name, long serverId, long serverRevisionId, boolean publishable, MediaKey iconMediaKey) {
        super(publishable, serverId, serverRevisionId);
        mGroup = group;
        mName = name;
        mIconMediaKey = iconMediaKey;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String getGroup() {
        return mGroup;
    }

    @Override
    public MediaKey getIconMediaKey() {
        return mIconMediaKey;
    }

    void setName(String name) {
        mName = name;
    }

    void setGroup(String group) {
        mGroup = group;
    }

    @Override
    public String toString() {
        return /*(mGroup != null && mGroup.length() > 0 ? mGroup + " " : "") + */mName;
    }

}
