package se.devscout.android.model;

import se.devscout.server.api.model.ActivityHistoryData;
import se.devscout.server.api.model.HistoryType;
import se.devscout.server.api.model.UserKey;

public class ActivityHistoryPropertiesBean extends HistoryPropertiesBean<ActivityHistoryData> {

    public ActivityHistoryPropertiesBean(UserKey userKey, ActivityHistoryData data) {
        super(HistoryType.ACTIVITY, userKey, data);
    }
}
