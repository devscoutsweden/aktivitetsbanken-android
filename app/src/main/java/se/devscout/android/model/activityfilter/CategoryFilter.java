package se.devscout.android.model.activityfilter;

public interface CategoryFilter extends ActivityFilter {
    String getGroup();

    String getName();

    long getServerId();
}
