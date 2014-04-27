package se.devscout.android.model;

import se.devscout.server.api.model.CategoryProperties;

import java.io.Serializable;

class CategoryPropertiesPojo implements CategoryProperties, Serializable {
    private String mName;
    private String mGroup;

    CategoryPropertiesPojo(String group, String name) {
        mGroup = group;
        mName = name;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String getGroup() {
        return mGroup;
    }

    void setName(String name) {
        mName = name;
    }

    void setGroup(String group) {
        mGroup = group;
    }
}
