package se.devscout.android.util;

import se.devscout.android.model.Range;
import se.devscout.server.api.activityfilter.AgeRangeFilter;
import se.devscout.server.api.activityfilter.BaseActivityFilterVisitor;

/**
 * Tests if the age interval of an activity intersects a certain age range.
 */
public class SimpleAgeRangeFilter extends SimpleRangeFilter implements AgeRangeFilter {

    public SimpleAgeRangeFilter(Range<Integer> range) {
        super(range);
    }

    @Override
    public <T> T visit(BaseActivityFilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
