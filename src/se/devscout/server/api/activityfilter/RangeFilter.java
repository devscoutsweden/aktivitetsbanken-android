package se.devscout.server.api.activityfilter;

import se.devscout.server.api.ActivityFilter;

interface RangeFilter extends ActivityFilter {
    Integer getMax();

    Integer getMin();
}
