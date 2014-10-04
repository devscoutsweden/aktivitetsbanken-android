package se.devscout.android.model.repo;

import com.fasterxml.jackson.annotation.JsonFilter;
import se.devscout.android.model.ActivityPropertiesBean;
import se.devscout.server.api.model.Activity;
import se.devscout.server.api.model.UserKey;

import java.io.Serializable;

@JsonFilter("ActivityBean")
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
