package se.devscout.android.util;

import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.ActivityFilterFactory;
import se.devscout.server.api.AverageRatingFilter;
import se.devscout.server.api.activityfilter.IsUserFavouriteFilter;
import se.devscout.server.api.activityfilter.OverallFavouriteActivitiesFilter;
import se.devscout.server.api.activityfilter.RandomActivitiesFilter;
import se.devscout.server.api.activityfilter.ServerObjectIdentifiersFilter;
import se.devscout.server.api.model.Range;
import se.devscout.server.api.model.ServerObjectIdentifier;
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

    @Override
    public SimpleOrFilter createOrFilter(ActivityFilter... filters) {
        SimpleOrFilter res = new SimpleOrFilter();
        fillCompoundFilter(res, filters);
        return res;
    }

    private void fillCompoundFilter(SimpleCompoundFilter res, ActivityFilter[] filters) {
        for (ActivityFilter filter : filters) {
            if (filter instanceof SimpleFilter) {
                SimpleFilter simpleFilter = (SimpleFilter) filter;
                res.getFilters().add(simpleFilter);
            } else {
                throw new IllegalArgumentException("Filter must be " + SimpleFilter.class.getSimpleName() + " object.");
            }
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
        throw new UnsupportedOperationException("Searching for favourites is not implemented.");
    }

    @Override
    public RandomActivitiesFilter createRandomActivitiesFilter(int numberOfActivities) {
        throw new UnsupportedOperationException("Returing random activities is not implemented.");
    }

    @Override
    public ServerObjectIdentifiersFilter createServerObjectIdentifierFilter(ServerObjectIdentifier identifier) {
        return null;
    }

    @Override
    public OverallFavouriteActivitiesFilter createOverallFavouriteActivitiesFilter(int numberOfActivities) {
        throw new UnsupportedOperationException("Returing overall favourite activities is not implemented.");
    }

    @Override
    public AverageRatingFilter createAverageRatingFilter(double limit) {
        throw new UnsupportedOperationException("Returning activities by average rating is not implemented.");
    }

}
