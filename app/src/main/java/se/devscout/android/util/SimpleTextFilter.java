package se.devscout.android.util;

import se.devscout.android.model.activityfilter.BaseActivityFilterVisitor;
import se.devscout.android.model.activityfilter.TextFilter;

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
