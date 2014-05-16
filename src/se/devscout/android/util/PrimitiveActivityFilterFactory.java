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
    public AgeRangeFilter createAgeRangeFilter(int min, int max) {
        return new AgeRangeFilter(new IntegerRangePojo(min, max));
    }

    @Override
    public TimeRangeFilter createTimeRangeFilter(int min, int max) {
        return new TimeRangeFilter(new IntegerRangePojo(min, max));
    }

    @Override
    public AndFilter createAndFilter(ActivityFilter... filters) {
        AndFilter res = new AndFilter();
        fillCompoundFilter(res, filters);
        return res;
    }

    @Override
    public OrFilter createOrFilter(ActivityFilter... filters) {
        OrFilter res = new OrFilter();
        fillCompoundFilter(res, filters);
        return res;
    }

    private void fillCompoundFilter(CompoundFilter res, ActivityFilter[] filters) {
        for (ActivityFilter filter : filters) {
            if (filter instanceof PrimitiveFilter) {
                PrimitiveFilter primitiveFilter = (PrimitiveFilter) filter;
                res.getFilters().add(primitiveFilter);
            } else {
                throw new IllegalArgumentException("Filter must be " + PrimitiveFilter.class.getSimpleName() + " object.");
            }
        }
    }

    @Override
    public CategoryFilter createCategoryFilter(String group, String name) {
        return new CategoryFilter(new CategoryPropertiesPojo(group, name));
    }

    @Override
    public TextFilter createTextFilter(String condition) {
        return new TextFilter(condition);
    }

    @Override
    public IsFeaturedFilter createIsFeaturedFilter() {
        return new IsFeaturedFilter();
    }

    @Override
    public IsUserFavouriteFilter createIsUserFavouriteFilter(UserKey userKey) throws ActivityFilterFactoryException {
        throw new ActivityFilterFactoryException("Searching for favourites is not implemented.");
    }

}
