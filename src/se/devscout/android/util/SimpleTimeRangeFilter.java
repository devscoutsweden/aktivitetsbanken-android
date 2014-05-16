package se.devscout.android.util;

import se.devscout.server.api.activityfilter.TimeRangeFilter;
import se.devscout.server.api.model.ActivityProperties;
import se.devscout.server.api.model.Range;

/**
 * Tests if the age interval of an activity intersects a certain age range.
 */
public class SimpleTimeRangeFilter extends SimpleRangeFilter implements TimeRangeFilter {

    public SimpleTimeRangeFilter(Range<Integer> range) {
        super(range);
    }

    @Override
    public boolean matches(ActivityProperties properties) {
        return isFullyWithin(ActivityUtil.getLatestActivityRevision(properties).getTimeActivity());
    }

}
