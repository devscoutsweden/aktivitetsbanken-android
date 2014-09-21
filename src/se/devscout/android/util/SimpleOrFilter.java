package se.devscout.android.util;

import android.net.Uri;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.ActivityFilterVisitor;
import se.devscout.server.api.URIBuilderActivityFilterVisitor;
import se.devscout.server.api.model.ActivityProperties;

public class SimpleOrFilter extends SimpleCompoundFilter implements se.devscout.server.api.activityfilter.OrFilter {

    @Override
    public boolean matches(ActivityProperties properties) {
        for (ActivityFilter filter : mFilters) {
            if (filter instanceof SimpleFilter) {
                SimpleFilter simpleFilter = (SimpleFilter) filter;
                if (simpleFilter.matches(properties)) {
                    return true;
                }
            }
        }
        return false;
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
