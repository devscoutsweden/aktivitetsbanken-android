package se.devscout.server.api.activityfilter;

import se.devscout.server.api.ActivityFilter;

public interface CategoryFilter extends ActivityFilter {
    String getGroup();

    String getName();

    int getServerId();
}
