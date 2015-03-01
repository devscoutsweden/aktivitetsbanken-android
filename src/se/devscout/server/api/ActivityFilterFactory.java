package se.devscout.server.api;

import se.devscout.android.util.SimpleAgeRangeFilter;
import se.devscout.server.api.activityfilter.*;
import se.devscout.server.api.model.Range;
import se.devscout.server.api.model.ServerObjectIdentifier;
import se.devscout.server.api.model.UserKey;

public interface ActivityFilterFactory {
    SimpleAgeRangeFilter createAgeRangeFilter(Range<Integer> range);

    TimeRangeFilter createTimeRangeFilter(Range<Integer> range);

    AndFilter createAndFilter(ActivityFilter... filters);

    OrFilter createOrFilter(ActivityFilter... filters);

    CategoryFilter createCategoryFilter(String group, String name, long serverId);

    TextFilter createTextFilter(String condition);

    IsFeaturedFilter createIsFeaturedFilter();

    IsUserFavouriteFilter createIsUserFavouriteFilter(UserKey userKey);

    RandomActivitiesFilter createRandomActivitiesFilter(int numberOfActivities);

    ServerObjectIdentifiersFilter createServerObjectIdentifierFilter(ServerObjectIdentifier identifier);

    OverallFavouriteActivitiesFilter createOverallFavouriteActivitiesFilter(int numberOfActivities);

    AverageRatingFilter createAverageRatingFilter(double limit);
}
