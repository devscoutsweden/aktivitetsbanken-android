package se.devscout.android.model;

public class ActivityHistoryPropertiesBean extends HistoryPropertiesBean<ActivityHistoryData> {

    public ActivityHistoryPropertiesBean(UserKey userKey, ActivityHistoryData data) {
        super(HistoryType.ACTIVITY, userKey, data);
    }
}
