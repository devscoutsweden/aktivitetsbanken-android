package se.devscout.server.api.activityfilter;

import se.devscout.server.api.ActivityFilter;

import java.util.ArrayList;

interface CompoundFilter extends ActivityFilter {
    ArrayList<ActivityFilter> getFilters();
}
