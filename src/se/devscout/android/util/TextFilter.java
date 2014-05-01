package se.devscout.android.util;

import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.ActivityProperties;

public class TextFilter implements ActivityFilter {
    private final String mCondition;

    public TextFilter(String condition) {
        mCondition = condition;
    }

    @Override
    public boolean matches(ActivityProperties properties) {
        return ActivityUtil.getLatestActivityRevision(properties).getName().contains(mCondition);
    }
}
