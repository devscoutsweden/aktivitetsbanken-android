package se.devscout.android.util;

import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.Range;

public abstract class RangeFilter implements ActivityFilter {
    protected final Integer mMin;
    protected final Integer mMax;

    public RangeFilter(Range<Integer> range) {
        mMin = range.getMin();
        mMax = range.getMax();
    }

    public boolean matches(Range<Integer> candidate) {
        if ((mMax > candidate.getMin() && mMax < candidate.getMax())
                ||
                (mMin < candidate.getMax() && mMin > candidate.getMin())) {
            return true;
        } else {
            return false;
        }
    }
}
