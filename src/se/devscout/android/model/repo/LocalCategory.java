package se.devscout.android.model.repo;

import se.devscout.android.model.CategoryPropertiesPojo;
import se.devscout.server.api.model.Category;

import java.io.Serializable;

class LocalCategory extends CategoryPropertiesPojo implements Category, Serializable {
    public static long debugCounter;
    private Long mId;

    LocalCategory(String group, String name, Long id) {
        super(group, name);
        mId = id;
    }

    @Override
    public Long getId() {
        return mId;
    }
}
