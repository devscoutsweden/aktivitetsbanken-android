package se.devscout.android.util;

import se.devscout.server.api.activityfilter.ActivityKeysFilter;
import se.devscout.server.api.activityfilter.BaseActivityFilterVisitor;
import se.devscout.server.api.model.ActivityKey;

public class SimpleActivityKeysFilter implements ActivityKeysFilter {
    private ActivityKey[] mActivityKeys;

    public SimpleActivityKeysFilter(ActivityKey... activityKeys) {
        mActivityKeys = activityKeys;
    }

    @Override
    public ActivityKey[] getActivityKeys() {
        return mActivityKeys;
    }

    @Override
    public <T> T visit(BaseActivityFilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
