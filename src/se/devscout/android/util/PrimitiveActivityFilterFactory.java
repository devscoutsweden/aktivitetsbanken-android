package se.devscout.android.util;

import se.devscout.server.api.activityfilter.*;
import se.devscout.server.api.model.Range;
import se.devscout.server.api.model.UserKey;

public class PrimitiveActivityFilterFactory implements ActivityFilterFactory {
    @Override
    public SimpleAgeRangeFilter createAgeRangeFilter(Range<Integer> range) {
        return new SimpleAgeRangeFilter(range);
    }

    @Override
    public SimpleTimeRangeFilter createTimeRangeFilter(Range<Integer> range) {
        return new SimpleTimeRangeFilter(range);
    }

    @Override
    public SimpleAndFilter createAndFilter(ActivityFilter... filters) {
        SimpleAndFilter res = new SimpleAndFilter();
        fillCompoundFilter(res, filters);
        return res;
    }

    private void fillCompoundFilter(SimpleCompoundFilter res, ActivityFilter[] filters) {
        for (ActivityFilter filter : filters) {
            res.getFilters().add(filter);
        }
    }

    @Override
    public SimpleCategoryFilter createCategoryFilter(String group, String name, long serverId) {
        return new SimpleCategoryFilter(group, name, serverId);
    }

    @Override
    public SimpleTextFilter createTextFilter(String condition) {
        return new SimpleTextFilter(condition);
    }

    @Override
    public SimpleIsFeaturedFilter createIsFeaturedFilter() {
        return new SimpleIsFeaturedFilter();
    }

    @Override
    public IsUserFavouriteFilter createIsUserFavouriteFilter(UserKey userKey) {
        return new SimpleIsUserFavouriteFilter(userKey);
    }

    @Override
    public RandomActivitiesFilter createRandomActivitiesFilter(int numberOfActivities) {
        return new SimpleRandomActivitiesFilter(numberOfActivities);
    }

    @Override
    public OverallFavouriteActivitiesFilter createOverallFavouriteActivitiesFilter(final int numberOfActivities) {
        return new SimpleOverallFavouriteActivitiesFilter(numberOfActivities);
    }

    @Override
    public AverageRatingFilter createAverageRatingFilter(double limit) {
        return new SimpleAverageRatingFilter(limit);
    }

}
