package se.devscout.server.api.activityfilter;

import java.util.ArrayList;

public interface CompoundFilter extends ActivityFilter {
    ArrayList<ActivityFilter> getFilters();
}
