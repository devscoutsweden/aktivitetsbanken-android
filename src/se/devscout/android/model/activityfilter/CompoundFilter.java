package se.devscout.android.model.activityfilter;

import java.util.ArrayList;

public interface CompoundFilter extends ActivityFilter {
    ArrayList<ActivityFilter> getFilters();
}
