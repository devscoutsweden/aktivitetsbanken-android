package se.devscout.android.util;

import se.devscout.android.model.ActivityKey;
import se.devscout.android.model.activityfilter.BaseActivityFilterVisitor;
import se.devscout.android.model.activityfilter.RelatedToFilter;

public class SimpleRelatedToFilter implements RelatedToFilter {
    private ActivityKey mActivityKey;

    public SimpleRelatedToFilter(ActivityKey activityKey) {
        mActivityKey = activityKey;
    }

    @Override
    public ActivityKey getActivityKey() {
        return mActivityKey;
    }

    @Override
    public <T> T visit(BaseActivityFilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
