package se.devscout.server.api.activityfilter;

import se.devscout.android.model.Range;
import se.devscout.android.model.UserKey;
import se.devscout.android.util.SimpleAgeRangeFilter;

public interface ActivityFilterFactory {
    SimpleAgeRangeFilter createAgeRangeFilter(Range<Integer> range);

    TimeRangeFilter createTimeRangeFilter(Range<Integer> range);

    AndFilter createAndFilter(ActivityFilter... filters);

    CategoryFilter createCategoryFilter(String group, String name, long serverId);

    TextFilter createTextFilter(String condition);

    IsFeaturedFilter createIsFeaturedFilter();

    IsUserFavouriteFilter createIsUserFavouriteFilter(UserKey userKey);

    RandomActivitiesFilter createRandomActivitiesFilter(int numberOfActivities);

    OverallFavouriteActivitiesFilter createOverallFavouriteActivitiesFilter(int numberOfActivities);

    AverageRatingFilter createAverageRatingFilter(double limit);
}
