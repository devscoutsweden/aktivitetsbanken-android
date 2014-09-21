package se.devscout.android.util;

import android.net.Uri;
import se.devscout.server.api.ActivityFilterVisitor;
import se.devscout.server.api.URIBuilderActivityFilterVisitor;
import se.devscout.server.api.model.ActivityProperties;
import se.devscout.server.api.model.Range;

/**
 * Tests if the age interval of an activity intersects a certain age range.
 */
public class SimpleAgeRangeFilter extends SimpleRangeFilter implements se.devscout.server.api.activityfilter.AgeRangeFilter {

    public SimpleAgeRangeFilter(Range<Integer> range) {
        super(range);
    }

    @Override
    public boolean matches(ActivityProperties properties) {
        return isPartlyWithin(properties.getAges());
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
