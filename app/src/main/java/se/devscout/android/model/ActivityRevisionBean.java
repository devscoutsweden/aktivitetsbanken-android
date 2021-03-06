package se.devscout.android.model;

import java.io.Serializable;

public class ActivityRevisionBean extends ActivityRevisionPropertiesBean implements ActivityRevision, Serializable {
    public static long debugCounter;
    private Long mId;

    public ActivityRevisionBean(String name, boolean featured, ActivityKey activityKey, Long revisionId) {
        super(name, featured, activityKey);
        mId = revisionId;
    }

    @Override
    public Long getId() {
        return mId;
    }
}
