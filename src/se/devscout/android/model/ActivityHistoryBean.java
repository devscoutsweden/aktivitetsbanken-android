package se.devscout.android.model;

import se.devscout.server.api.model.ActivityHistory;
import se.devscout.server.api.model.ActivityHistoryData;
import se.devscout.server.api.model.UserKey;

public class ActivityHistoryBean extends ActivityHistoryPropertiesBean implements ActivityHistory {

    public ActivityHistoryBean(Long id, UserKey userKey, ActivityHistoryData data) {
        super(userKey, data);
        mId = id;
    }

    private Long mId;

    @Override
    public Long getId() {
        return mId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActivityHistoryBean that = (ActivityHistoryBean) o;

        Long thisDataFilter = getData().getId();
        Long thatDataFilter = that.getData().getId();
        if (thisDataFilter != null ? !thisDataFilter.equals(thatDataFilter) : thatDataFilter != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        ActivityHistoryData data = getData();
        return data != null && data.getId() != null ? data.getId().hashCode() : 0;
    }

}
