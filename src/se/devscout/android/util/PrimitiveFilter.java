package se.devscout.android.util;

import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.ActivityProperties;

public abstract class PrimitiveFilter implements ActivityFilter {
    public abstract boolean matches(ActivityProperties properties);
/*
    public boolean matches(ActivityProperties properties) {
        throw new UnsupportedOperationException();
    }
*/

    public static PrimitiveFilter fromActivityFilter(ActivityFilter activityFilter) {
        return null;
    }
}
