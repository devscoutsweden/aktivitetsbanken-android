package se.devscout.android.util;

import se.devscout.server.api.BaseActivityFilterVisitor;
import se.devscout.server.api.activityfilter.AndFilter;

public class SimpleAndFilter extends SimpleCompoundFilter implements AndFilter {

    @Override
    public <T> T visit(BaseActivityFilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
