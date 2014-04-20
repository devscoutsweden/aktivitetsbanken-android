package se.devscout.android.util;

import se.devscout.server.api.ActivityFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CompoundFilter implements ActivityFilter {
    protected final List<ActivityFilter> mFilters;

    public CompoundFilter() {
        this(new ArrayList<ActivityFilter>());
    }

    public CompoundFilter(ActivityFilter... filters) {
        this(Arrays.asList(filters));
    }

    public CompoundFilter(List<ActivityFilter> filters) {
        mFilters = filters;
    }

    public void addFilter(ActivityFilter filter) {
        mFilters.add(filter);
    }

    public boolean isEmpty() {
        return mFilters.isEmpty();
    }
}
