package se.devscout.android.model.repo.sql;

import android.net.Uri;
import se.devscout.android.util.SimpleFilter;
import se.devscout.server.api.ActivityFilterVisitor;
import se.devscout.server.api.URIBuilderActivityFilterVisitor;
import se.devscout.server.api.activityfilter.RandomActivitiesFilter;
import se.devscout.server.api.model.ActivityProperties;

public class SQLRandomActivitiesFilter extends SimpleFilter implements SQLActivityFilter, RandomActivitiesFilter {
    private int mNumberOfActivities;

    public SQLRandomActivitiesFilter(int numberOfActivities) {
        mNumberOfActivities = numberOfActivities;
    }

    @Override
    public int getNumberOfActivities() {
        return mNumberOfActivities;
    }

    @Override
    public String toString(ActivityFilterVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void applyFilter(QueryBuilder queryBuilder) {
        queryBuilder.addRandomlySelectedRows(mNumberOfActivities);
    }

    @Override
    public boolean matches(ActivityProperties properties) {
        return true; // SQL takes care of everything
    }

    @Override
    public Uri toAPIRequest(URIBuilderActivityFilterVisitor visitor) {
        return visitor.visit(this);
    }
}
