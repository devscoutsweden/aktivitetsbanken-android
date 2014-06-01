package se.devscout.server.api.activityfilter;

import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.ActivityKey;

public interface ActivityKeyFilter extends ActivityFilter {
    ActivityKey getActivityKey();
}
