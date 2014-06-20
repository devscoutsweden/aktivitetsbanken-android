package se.devscout.android.util;

import se.devscout.android.model.CategoryPropertiesPojo;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.ActivityFilterFactory;
import se.devscout.server.api.RandomActivitiesFilter;
import se.devscout.server.api.activityfilter.IsUserFavouriteFilter;
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
    public SimpleCategoryFilter createCategoryFilter(String group, String name) {
        return new SimpleCategoryFilter(new CategoryPropertiesPojo(group, name));
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

}
