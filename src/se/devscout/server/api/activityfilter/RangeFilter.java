package se.devscout.server.api.activityfilter;

public interface RangeFilter extends ActivityFilter {
    Integer getMax();

    Integer getMin();
}
