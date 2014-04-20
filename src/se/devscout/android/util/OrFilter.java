package se.devscout.android.util;

import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.ActivityProperties;

public class OrFilter extends CompoundFilter {

    @Override
    public boolean matches(ActivityProperties properties) {
        for (ActivityFilter filter : mFilters) {
            if (filter.matches(properties)) {
                return true;
            }
        }
        return false;
    }
}
