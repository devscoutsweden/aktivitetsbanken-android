package se.devscout.server.api.activityfilter;

public interface CategoryFilter extends ActivityFilter {
    String getGroup();

    String getName();

    long getServerId();
}
