package se.devscout.android.model;

import java.io.Serializable;

public class ActivityBean extends ActivityPropertiesBean implements Activity, Serializable, Comparable<ActivityBean> {
    public static long debugCounter;
    private Long mId;

    public ActivityBean(UserKey owner, Long id, long serverId, long serverRevisionId, boolean publishable) {
        super(publishable, serverId, serverRevisionId, owner);
        mId = id;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(ActivityBean activityBean) {
        return activityBean != null ? getName().compareTo(activityBean.getName()) : 0;
    }

    @Override
    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }
}
