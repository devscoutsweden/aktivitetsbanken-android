package se.devscout.server.api.activityfilter;

import se.devscout.server.api.ActivityFilter;

public interface AverageRatingFilter extends ActivityFilter {
    double getLimit();
}
