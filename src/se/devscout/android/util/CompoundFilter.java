package se.devscout.android.util;

import se.devscout.server.api.ActivityFilter;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class CompoundFilter extends PrimitiveFilter {
    protected final ArrayList<ActivityFilter> mFilters;

    public CompoundFilter() {
        this(new ArrayList<ActivityFilter>());
    }

    public CompoundFilter(ActivityFilter... filters) {
        this(new ArrayList(Arrays.asList(filters)));
    }

    private CompoundFilter(ArrayList<ActivityFilter> filters) {
        mFilters = filters;
    }

    public ArrayList<ActivityFilter> getFilters() {
        return mFilters;
    }

    public void addFilter(ActivityFilter filter) {
        mFilters.add(filter);
    }

    public boolean isEmpty() {
        return mFilters.isEmpty();
    }
}
