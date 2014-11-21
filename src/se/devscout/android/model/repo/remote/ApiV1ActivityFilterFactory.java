package se.devscout.android.model.repo.remote;

import se.devscout.android.model.repo.sql.SQLActivityFilterFactory;
import se.devscout.server.api.activityfilter.OverallFavouriteActivitiesFilter;

class ApiV1ActivityFilterFactory extends SQLActivityFilterFactory {
    @Override
    public OverallFavouriteActivitiesFilter createOverallFavouriteActivitiesFilter(final int numberOfActivities) {
        return new OverallFavouriteActivitiesFilterPojo(numberOfActivities);
    }

}