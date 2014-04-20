package se.devscout.android.util;

import se.devscout.server.api.ActivityFilter;
import se.devscout.shared.data.model.ActivityProperties;

/**
 * Tests if activity is marked a Featured.
 */
public class IsFeaturedFilter implements ActivityFilter {
    @Override
    public boolean matches(ActivityProperties properties) {
        return properties.getRevisions().get(0).isFeatured();
    }
}
