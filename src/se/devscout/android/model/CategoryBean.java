package se.devscout.android.model;

import se.devscout.server.api.model.Category;
import se.devscout.server.api.model.MediaKey;

import java.io.Serializable;

public class CategoryBean extends CategoryPropertiesBean implements Category, Serializable {
    public static long debugCounter;
    private Long mId;

    public CategoryBean(String group, String name, Long id, long serverId, long serverRevisionId, MediaKey iconMediaKey, Integer mActivitiesCount) {
        super(group, name, serverId, serverRevisionId, false, iconMediaKey, mActivitiesCount);
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
