package se.devscout.server.api.activityfilter;

import se.devscout.server.api.model.ActivityKey;

public interface ActivityKeysFilter extends ActivityFilter {
    ActivityKey[] getActivityKeys();
}
