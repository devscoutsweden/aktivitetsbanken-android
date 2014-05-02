package se.devscout.android.util;

import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.ActivityProperties;

import java.io.Serializable;

public class TextFilter implements ActivityFilter, Serializable {
    private final String mCondition;

    public TextFilter(String condition) {
        mCondition = condition;
    }

    @Override
    public boolean matches(ActivityProperties properties) {
        return ActivityUtil.getLatestActivityRevision(properties).getName().contains(mCondition);
    }
}
