package se.devscout.android.util;

import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.ActivityProperties;

public class SimpleAndFilter extends SimpleCompoundFilter implements se.devscout.server.api.activityfilter.AndFilter {

    @Override
    public boolean matches(ActivityProperties properties) {
        for (ActivityFilter filter : mFilters) {
            if (filter instanceof SimpleFilter) {
                SimpleFilter simpleFilter = (SimpleFilter) filter;
                if (!simpleFilter.matches(properties)) {
                    return false;
                }
            }
        }
        return true;
    }
}
