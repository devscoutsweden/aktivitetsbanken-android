package se.devscout.server.api.activityfilter;

import se.devscout.android.model.ActivityKey;

public interface ActivityKeysFilter extends ActivityFilter {
    ActivityKey[] getActivityKeys();
}
