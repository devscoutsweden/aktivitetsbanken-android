package se.devscout.android.util;

import se.devscout.android.model.activityfilter.BaseActivityFilterVisitor;
import se.devscout.android.model.activityfilter.OverallFavouriteActivitiesFilter;

public class SimpleOverallFavouriteActivitiesFilter implements OverallFavouriteActivitiesFilter {

    private final int numberOfActivities;

    public SimpleOverallFavouriteActivitiesFilter(int numberOfActivities) {
        this.numberOfActivities = numberOfActivities;
    }

    @Override
    public int getNumberOfActivities() {
        return numberOfActivities;
    }

    @Override
    public <T> T visit(BaseActivityFilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
