package se.devscout.android.util;

import se.devscout.android.model.activityfilter.BaseActivityFilterVisitor;
import se.devscout.android.model.activityfilter.IsFeaturedFilter;

/**
 * Tests if activity is marked a Featured.
 */
public class SimpleIsFeaturedFilter implements IsFeaturedFilter {
    @Override
    public <T> T visit(BaseActivityFilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
