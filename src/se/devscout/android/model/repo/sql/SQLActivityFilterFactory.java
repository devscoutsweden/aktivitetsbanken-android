package se.devscout.android.model.repo.sql;

import se.devscout.android.util.PrimitiveActivityFilterFactory;
import se.devscout.android.util.SimpleAgeRangeFilter;
import se.devscout.android.util.SimpleIsFeaturedFilter;
import se.devscout.android.util.SimpleTextFilter;
import se.devscout.server.api.RandomActivitiesFilter;
import se.devscout.server.api.activityfilter.IsUserFavouriteFilter;
import se.devscout.server.api.model.Range;
import se.devscout.server.api.model.UserKey;

class SQLActivityFilterFactory extends PrimitiveActivityFilterFactory {
    private final UserKey mAnonymousUserKey;

    public SQLActivityFilterFactory(UserKey anonymousUserKey) {
        mAnonymousUserKey = anonymousUserKey;
    }

    @Override
    public IsUserFavouriteFilter createIsUserFavouriteFilter(UserKey userKey) {
        return new SQLIsUserFavouriteFilter(mAnonymousUserKey);
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
    public RandomActivitiesFilter createRandomActivitiesFilter(int numberOfActivities) {
        return new SQLRandomActivitiesFilter(numberOfActivities);
    }
}
