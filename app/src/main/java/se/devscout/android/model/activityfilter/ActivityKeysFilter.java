package se.devscout.android.model.activityfilter;

import se.devscout.android.model.ActivityKey;

public interface ActivityKeysFilter extends ActivityFilter {
    ActivityKey[] getActivityKeys();
}
