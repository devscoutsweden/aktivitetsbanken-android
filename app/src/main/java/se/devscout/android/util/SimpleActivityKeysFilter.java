package se.devscout.android.util;

import se.devscout.android.model.ActivityKey;
import se.devscout.android.model.activityfilter.ActivityKeysFilter;
import se.devscout.android.model.activityfilter.BaseActivityFilterVisitor;

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
