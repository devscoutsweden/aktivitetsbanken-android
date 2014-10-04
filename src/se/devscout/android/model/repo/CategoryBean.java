package se.devscout.android.model.repo;

import se.devscout.android.model.CategoryPropertiesBean;
import se.devscout.server.api.model.Category;

import java.io.Serializable;

public class CategoryBean extends CategoryPropertiesBean implements Category, Serializable {
    public static long debugCounter;
    private Long mId;

    public CategoryBean(String group, String name, Long id, long serverId, long serverRevisionId) {
        super(group, name, serverId, serverRevisionId, false);
        mId = id;
    }

    @Override
    public Long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }
}
