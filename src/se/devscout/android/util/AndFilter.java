package se.devscout.android.util;

import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.ActivityProperties;

public class AndFilter extends CompoundFilter {
    public AndFilter() {
    }

    public AndFilter(ActivityFilter... filters) {
        super(filters);
    }

    @Override
    public boolean matches(ActivityProperties properties) {
        for (ActivityFilter filter : mFilters) {
            if (!filter.matches(properties)) {
                return false;
            }
        }
        return true;
    }
}
