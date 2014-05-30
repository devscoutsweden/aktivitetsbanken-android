package se.devscout.android.util;

import se.devscout.server.api.ActivityFilterVisitor;
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
        return isPartlyWithin(ActivityUtil.getLatestActivityRevision(properties).getAges());
    }

    @Override
    public String toString(ActivityFilterVisitor visitor) {
        return visitor.visit(this);
    }
}
