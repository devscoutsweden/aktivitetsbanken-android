package se.devscout.server.api.activityfilter;

import se.devscout.server.api.ActivityFilter;

public interface TextFilter  extends ActivityFilter {
    String getCondition();
}
