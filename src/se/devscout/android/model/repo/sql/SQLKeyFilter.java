package se.devscout.android.model.repo.sql;

import se.devscout.android.util.SimpleFilter;
import se.devscout.server.api.ActivityFilterVisitor;
import se.devscout.server.api.activityfilter.ActivityKeyFilter;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.ActivityProperties;

class SQLKeyFilter extends SimpleFilter implements ActivityKeyFilter, SQLActivityFilter {
    private ActivityKey mActivityKey;

    public SQLKeyFilter(ActivityKey activityKey) {
        mActivityKey = activityKey;
    }

    @Override
    public ActivityKey getActivityKey() {
        return mActivityKey;
    }

    @Override
    public boolean matches(ActivityProperties properties) {
        return true;
    }

    @Override
    public void applyFilter(QueryBuilder queryBuilder) {
        queryBuilder.addWhereActivity(mActivityKey);
    }

    @Override
    public String toString(ActivityFilterVisitor visitor) {
        return visitor.visit(this);
    }
}
