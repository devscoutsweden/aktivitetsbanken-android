package se.devscout.android.model.repo.sql;

import android.net.Uri;
import se.devscout.android.util.SimpleFilter;
import se.devscout.server.api.ActivityFilterVisitor;
import se.devscout.server.api.URIBuilderActivityFilterVisitor;
import se.devscout.server.api.activityfilter.ActivityKeysFilter;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.ActivityProperties;

class SQLKeysFilter extends SimpleFilter implements ActivityKeysFilter, SQLActivityFilter {
    private ActivityKey[] mActivityKeys;

    public SQLKeysFilter(ActivityKey... activityKeys) {
        mActivityKeys = activityKeys;
    }

    @Override
    public ActivityKey[] getActivityKeys() {
        return mActivityKeys;
    }

    @Override
    public boolean matches(ActivityProperties properties) {
        return true;
    }

    @Override
    public void applyFilter(QueryBuilder queryBuilder) {
        queryBuilder.addWhereActivities(mActivityKeys);
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