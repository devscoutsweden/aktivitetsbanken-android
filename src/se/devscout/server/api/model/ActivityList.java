package se.devscout.server.api.model;

import java.util.ArrayList;

/**
 * Custom List to enable key-based activity retrieval and to enable ActivityBack
 * implementations to customize the retrieval behaviour.
 */
public class ActivityList extends ArrayList<Activity> {
    public Activity get(ActivityKey activityKey) {
        for (Activity activity : this) {
            if (activity.getId().equals(activityKey.getId())) {
                return activity;
            }
        }
        return null;
    }
}
