package se.devscout.android.util;

import se.devscout.android.model.activityfilter.ActivityFilter;

import java.util.ArrayList;
import java.util.Arrays;

abstract class SimpleCompoundFilter implements ActivityFilter {
    final ArrayList<ActivityFilter> mFilters;

    SimpleCompoundFilter() {
        this(new ArrayList<ActivityFilter>());
    }

    public SimpleCompoundFilter(ActivityFilter... filters) {
        this(new ArrayList(Arrays.asList(filters)));
    }

    private SimpleCompoundFilter(ArrayList<ActivityFilter> filters) {
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
