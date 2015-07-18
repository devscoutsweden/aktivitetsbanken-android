package se.devscout.android.util;

import se.devscout.android.model.activityfilter.BaseActivityFilterVisitor;
import se.devscout.android.model.activityfilter.RandomActivitiesFilter;

class SimpleRandomActivitiesFilter implements RandomActivitiesFilter {
    private int mNumberOfActivities;

    public SimpleRandomActivitiesFilter(int numberOfActivities) {
        mNumberOfActivities = numberOfActivities;
    }

    @Override
    public int getNumberOfActivities() {
        return mNumberOfActivities;
    }
    @Override
    public <T> T visit(BaseActivityFilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
