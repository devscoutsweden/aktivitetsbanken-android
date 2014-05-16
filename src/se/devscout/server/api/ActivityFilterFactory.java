package se.devscout.server.api;

import se.devscout.server.api.activityfilter.*;
import se.devscout.server.api.model.UserKey;

public interface ActivityFilterFactory {
    se.devscout.android.util.AgeRangeFilter createAgeRangeFilter(int min, int max);

    TimeRangeFilter createTimeRangeFilter(int min, int max);

    AndFilter createAndFilter(ActivityFilter... filters);

    OrFilter createOrFilter(ActivityFilter... filters);

    CategoryFilter createCategoryFilter(String group, String name);

    TextFilter createTextFilter(String condition);

    IsFeaturedFilter createIsFeaturedFilter();

    IsUserFavouriteFilter createIsUserFavouriteFilter(UserKey userKey) throws ActivityFilterFactoryException;
}
