package se.devscout.android.util;

import se.devscout.server.api.model.ActivityProperties;

public class SimpleTextFilter extends SimpleFilter implements se.devscout.server.api.activityfilter.TextFilter {
    private final String mCondition;

    public SimpleTextFilter(String condition) {
        mCondition = condition;
    }

    @Override
    public String getCondition() {
        return mCondition;
    }

    @Override
    public boolean matches(ActivityProperties properties) {
        return ActivityUtil.getLatestActivityRevision(properties).getName().contains(mCondition);
    }
}
