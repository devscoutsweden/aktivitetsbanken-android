package se.devscout.android.model.repo.remote;

import android.net.Uri;
import se.devscout.server.api.ActivityFilterVisitor;
import se.devscout.server.api.AverageRatingFilter;
import se.devscout.server.api.URIBuilderActivityFilterVisitor;

public class AverageRatingFilterPojo implements AverageRatingFilter {
    private double mLimit;

    public AverageRatingFilterPojo(double limit) {
        mLimit = limit;
    }

    @Override
    public double getLimit() {
        return mLimit;
    }

    @Override
    public String toString(ActivityFilterVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Uri toAPIRequest(URIBuilderActivityFilterVisitor visitor) {
        return visitor.visit(this);
    }
}
