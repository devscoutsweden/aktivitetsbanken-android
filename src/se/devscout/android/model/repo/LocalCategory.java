package se.devscout.android.model.repo;

import se.devscout.android.model.CategoryPropertiesPojo;
import se.devscout.server.api.model.Category;

import java.io.Serializable;

public class LocalCategory extends CategoryPropertiesPojo implements Category, Serializable {
    public static long debugCounter;
    private Long mId;

    public LocalCategory(String group, String name, Long id, int serverId) {
        super(group, name, serverId, false);
        mId = id;
    }

    @Override
    public Long getId() {
        return mId;
    }
}
