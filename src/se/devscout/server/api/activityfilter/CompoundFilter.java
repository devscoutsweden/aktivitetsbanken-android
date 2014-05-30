package se.devscout.server.api.activityfilter;

import se.devscout.server.api.ActivityFilter;

import java.util.ArrayList;

public interface CompoundFilter extends ActivityFilter {
    ArrayList<ActivityFilter> getFilters();
}
