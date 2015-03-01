package se.devscout.android.util;

import se.devscout.server.api.BaseActivityFilterVisitor;
import se.devscout.server.api.activityfilter.TextFilter;

public class SimpleTextFilter implements TextFilter {
    private final String mCondition;

    public SimpleTextFilter(String condition) {
        mCondition = condition.trim();
    }

    @Override
    public String getCondition() {
        return mCondition;
    }

    @Override
    public <T> T visit(BaseActivityFilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
