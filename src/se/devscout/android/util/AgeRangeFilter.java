package se.devscout.android.util;

import se.devscout.server.api.model.ActivityProperties;
import se.devscout.server.api.model.Range;

/**
 * Tests if the age interval of an activity intersects a certain age range.
 */
public class AgeRangeFilter extends RangeFilter {

    public AgeRangeFilter(Range<Integer> range) {
        super(range);
    }

    @Override
    public boolean matches(ActivityProperties properties) {
        return matches(ActivityUtil.getLatestActivityRevision(properties).getAges());
    }

}
