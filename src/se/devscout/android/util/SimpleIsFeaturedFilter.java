package se.devscout.android.util;

import se.devscout.server.api.activityfilter.BaseActivityFilterVisitor;
import se.devscout.server.api.activityfilter.IsFeaturedFilter;

/**
 * Tests if activity is marked a Featured.
 */
public class SimpleIsFeaturedFilter implements IsFeaturedFilter {
    @Override
    public <T> T visit(BaseActivityFilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
