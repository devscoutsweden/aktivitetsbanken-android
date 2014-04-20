package se.devscout.android.util;

import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.ActivityProperties;

public class AndFilter extends CompoundFilter {

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
