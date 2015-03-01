package se.devscout.android.util;

import se.devscout.server.api.BaseActivityFilterVisitor;
import se.devscout.server.api.activityfilter.TimeRangeFilter;
import se.devscout.server.api.model.Range;

/**
 * Tests if the age interval of an activity intersects a certain age range.
 */
public class SimpleTimeRangeFilter extends SimpleRangeFilter implements TimeRangeFilter {

    public SimpleTimeRangeFilter(Range<Integer> range) {
        super(range);
    }

    @Override
    public <T> T visit(BaseActivityFilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
