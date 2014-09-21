package se.devscout.android.util;

import android.net.Uri;
import se.devscout.server.api.ActivityFilterVisitor;
import se.devscout.server.api.URIBuilderActivityFilterVisitor;
import se.devscout.server.api.model.ActivityProperties;

/**
 * Tests if activity is marked a Featured.
 */
public class SimpleIsFeaturedFilter extends SimpleFilter implements se.devscout.server.api.activityfilter.IsFeaturedFilter {
    @Override
    public boolean matches(ActivityProperties properties) {
        return properties.isFeatured();
    }

    @Override
    public String toString(ActivityFilterVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Uri toAPIRequest(URIBuilderActivityFilterVisitor visitor) {
        return visitor.visit(this);
    }
}
