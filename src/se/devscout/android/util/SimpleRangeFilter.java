package se.devscout.android.util;

import se.devscout.android.model.Range;

import java.io.Serializable;

public abstract class SimpleRangeFilter implements Serializable {
    private final Integer mMin;
    private final Integer mMax;

    SimpleRangeFilter(Range<Integer> range) {
        mMin = range.getMin();
        mMax = range.getMax();
    }

    public Integer getMax() {
        return mMax;
    }

    public Integer getMin() {
        return mMin;
    }

    //TODO: Needs unit tests
    public boolean isPartlyWithin(Range<Integer> candidate) {
        boolean isCandidateBeforeRange = mMin >= candidate.getMax();
        boolean isCandidateAfterRange = mMax <= candidate.getMin();
        boolean match = !(isCandidateBeforeRange || isCandidateAfterRange);
//        LogUtil.d(getClass().getName(), "isPartlyWithin " + toString() + " candidate=" + candidate.toString() + " match: " + match);
        return match;
    }

    //TODO: Needs unit tests
    boolean isFullyWithin(Range<Integer> candidate) {
        boolean startsAfter = mMin <= candidate.getMin();
        boolean endsBefore = mMax >= candidate.getMax();
        boolean match = startsAfter && endsBefore;
//        LogUtil.d(getClass().getName(), "isFullyWithin " + toString() + " candidate=" + candidate.toString() + " match: " + match);
        return match;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + mMin + " - " + mMax;
    }
}
