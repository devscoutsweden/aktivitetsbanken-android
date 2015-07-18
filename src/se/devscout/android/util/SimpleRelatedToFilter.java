package se.devscout.android.util;

import se.devscout.server.api.BaseActivityFilterVisitor;
import se.devscout.server.api.activityfilter.RelatedToFilter;
import se.devscout.server.api.model.ActivityKey;

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
