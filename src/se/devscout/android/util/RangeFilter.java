package se.devscout.android.util;

import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.Range;

public abstract class RangeFilter implements ActivityFilter {
    protected final Range<Integer> mRange;

    public RangeFilter(Range<Integer> range) {
        mRange = range;
    }

    protected boolean matches(Range<Integer> candidate) {
        if ((mRange.getMax() >= candidate.getMin() && mRange.getMax() <= candidate.getMax())
                ||
                (mRange.getMin() <= candidate.getMax() && mRange.getMin() >= candidate.getMin())) {
            return true;
        } else {
            return false;
        }
    }
}
