package se.devscout.android.util;

import se.devscout.android.model.CategoryPropertiesPojo;
import se.devscout.android.model.IntegerRangePojo;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.ActivityFilterFactory;
import se.devscout.server.api.ActivityFilterFactoryException;
import se.devscout.server.api.activityfilter.IsUserFavouriteFilter;
import se.devscout.server.api.model.UserKey;

public class PrimitiveActivityFilterFactory implements ActivityFilterFactory {
    @Override
    public SimpleAgeRangeFilter createAgeRangeFilter(int min, int max) {
        return new SimpleAgeRangeFilter(new IntegerRangePojo(min, max));
    }

    @Override
    public SimpleTimeRangeFilter createTimeRangeFilter(int min, int max) {
        return new SimpleTimeRangeFilter(new IntegerRangePojo(min, max));
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
    public IsUserFavouriteFilter createIsUserFavouriteFilter(UserKey userKey) throws ActivityFilterFactoryException {
        throw new ActivityFilterFactoryException("Searching for favourites is not implemented.");
    }

}
