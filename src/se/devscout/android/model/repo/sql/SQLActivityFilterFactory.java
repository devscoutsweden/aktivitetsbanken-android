package se.devscout.android.model.repo.sql;

import se.devscout.android.util.*;
import se.devscout.server.api.RandomActivitiesFilter;
import se.devscout.server.api.activityfilter.IsUserFavouriteFilter;
import se.devscout.server.api.model.Range;
import se.devscout.server.api.model.UserKey;

class SQLActivityFilterFactory extends PrimitiveActivityFilterFactory {

    public SQLActivityFilterFactory() {
    }

    @Override
    public IsUserFavouriteFilter createIsUserFavouriteFilter(UserKey userKey) {
        return new SQLIsUserFavouriteFilter(userKey);
    }

    @Override
    public SimpleIsFeaturedFilter createIsFeaturedFilter() {
        return new SQLIsFeaturedFilter();
    }

    @Override
    public SimpleTextFilter createTextFilter(String condition) {
        return new SQLTextFilter(condition);
    }

    @Override
    public SimpleAgeRangeFilter createAgeRangeFilter(Range<Integer> range) {
        return new SQLAgeRangeFilter(range);
    }

    @Override
    public SimpleTimeRangeFilter createTimeRangeFilter(Range<Integer> range) {
        return new SQLTimeRangeFilter(range);
    }

    @Override
    public RandomActivitiesFilter createRandomActivitiesFilter(int numberOfActivities) {
        return new SQLRandomActivitiesFilter(numberOfActivities);
    }
}
