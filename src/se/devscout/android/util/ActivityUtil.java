package se.devscout.android.util;

import se.devscout.server.api.model.ActivityProperties;
import se.devscout.server.api.model.ActivityRevision;

public class ActivityUtil {
    public static ActivityRevision getLatestActivityRevision(ActivityProperties properties) {
        return properties.getRevisions().get(properties.getRevisions().size() - 1);
    }
}
