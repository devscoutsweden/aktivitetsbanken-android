package se.devscout.android.util;

import se.devscout.server.api.activityfilter.AndFilter;
import se.devscout.server.api.activityfilter.BaseActivityFilterVisitor;

public class SimpleAndFilter extends SimpleCompoundFilter implements AndFilter {

    @Override
    public <T> T visit(BaseActivityFilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
