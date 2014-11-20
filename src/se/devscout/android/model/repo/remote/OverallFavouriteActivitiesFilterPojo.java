package se.devscout.android.model.repo.remote;

import android.net.Uri;
import se.devscout.server.api.ActivityFilterVisitor;
import se.devscout.server.api.URIBuilderActivityFilterVisitor;
import se.devscout.server.api.activityfilter.OverallFavouriteActivitiesFilter;

class OverallFavouriteActivitiesFilterPojo implements OverallFavouriteActivitiesFilter {

    private final int numberOfActivities;

    public OverallFavouriteActivitiesFilterPojo(int numberOfActivities) {
        this.numberOfActivities = numberOfActivities;
    }

    @Override
    public int getNumberOfActivities() {
        return numberOfActivities;
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
