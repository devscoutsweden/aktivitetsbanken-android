package se.devscout.android.util;

import android.net.Uri;
import se.devscout.server.api.ActivityFilterVisitor;
import se.devscout.server.api.URIBuilderActivityFilterVisitor;
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
        return properties.getName().contains(mCondition);
    }

    @Override
    public String toString(ActivityFilterVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Uri toAPIRequest(URIBuilderActivityFilterVisitor visitor) {
        return visitor.visit(this);
    }
}
