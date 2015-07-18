package se.devscout.android.util;

import se.devscout.android.model.activityfilter.AverageRatingFilter;
import se.devscout.android.model.activityfilter.BaseActivityFilterVisitor;

public class SimpleAverageRatingFilter implements AverageRatingFilter {
    private double mLimit;

    public SimpleAverageRatingFilter(double limit) {
        mLimit = limit;
    }

    @Override
    public double getLimit() {
        return mLimit;
    }

    @Override
    public <T> T visit(BaseActivityFilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
