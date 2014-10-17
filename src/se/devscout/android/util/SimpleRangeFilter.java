package se.devscout.android.util;

import se.devscout.server.api.model.Range;

public abstract class SimpleRangeFilter extends SimpleFilter {
    protected final Integer mMin;
    protected final Integer mMax;

    public SimpleRangeFilter(Range<Integer> range) {
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
    public boolean isFullyWithin(Range<Integer> candidate) {
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
