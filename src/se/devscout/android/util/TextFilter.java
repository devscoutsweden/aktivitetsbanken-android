package se.devscout.android.util;

import se.devscout.server.api.model.ActivityProperties;

public class TextFilter extends PrimitiveFilter implements se.devscout.server.api.activityfilter.TextFilter {
    private final String mCondition;

    public TextFilter(String condition) {
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
