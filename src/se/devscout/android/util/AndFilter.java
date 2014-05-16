package se.devscout.android.util;

import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.ActivityProperties;

public class AndFilter extends CompoundFilter implements se.devscout.server.api.activityfilter.AndFilter {

    @Override
    public boolean matches(ActivityProperties properties) {
        for (ActivityFilter filter : mFilters) {
            if (filter instanceof PrimitiveFilter) {
                PrimitiveFilter primitiveFilter = (PrimitiveFilter) filter;
                if (!primitiveFilter.matches(properties)) {
                    return false;
                }
            }
        }
        return true;
    }
}
