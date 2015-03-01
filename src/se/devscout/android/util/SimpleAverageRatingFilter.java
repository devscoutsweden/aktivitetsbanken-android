package se.devscout.android.util;

import se.devscout.server.api.AverageRatingFilter;
import se.devscout.server.api.BaseActivityFilterVisitor;

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
