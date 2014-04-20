package se.devscout.android.util;

import se.devscout.server.api.ActivityFilter;
import se.devscout.shared.data.model.ActivityProperties;
import se.devscout.shared.data.model.Range;

/**
 * Tests if the age interval of an activity intersects a certain age range.
 */
public class AgeRangeFilter implements ActivityFilter {
    private final Range<Integer> mRange;

    public AgeRangeFilter(Range<Integer> range) {
        mRange = range;
    }

    @Override
    public boolean matches(ActivityProperties properties) {
        Range<Integer> candidateAgeRange = properties.getRevisions().get(0).getAges();
        return matches(candidateAgeRange);
    }

    private boolean matches(Range<Integer> candidate) {
        if ((mRange.getMax() >= candidate.getMin() && mRange.getMax() <= candidate.getMax())
                ||
                (mRange.getMin() <= candidate.getMax() && mRange.getMin() >= candidate.getMin())) {
            return true;
        } else {
            return false;
        }
    }
}
