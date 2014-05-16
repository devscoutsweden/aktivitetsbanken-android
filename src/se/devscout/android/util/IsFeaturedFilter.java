package se.devscout.android.util;

import se.devscout.server.api.model.ActivityProperties;

/**
 * Tests if activity is marked a Featured.
 */
public class IsFeaturedFilter extends PrimitiveFilter implements se.devscout.server.api.activityfilter.IsFeaturedFilter {
    @Override
    public boolean matches(ActivityProperties properties) {
        return ActivityUtil.getLatestActivityRevision(properties).isFeatured();
    }
}
