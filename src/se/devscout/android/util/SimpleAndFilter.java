package se.devscout.android.util;

import se.devscout.android.model.activityfilter.AndFilter;
import se.devscout.android.model.activityfilter.BaseActivityFilterVisitor;

public class SimpleAndFilter extends SimpleCompoundFilter implements AndFilter {

    @Override
    public <T> T visit(BaseActivityFilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
