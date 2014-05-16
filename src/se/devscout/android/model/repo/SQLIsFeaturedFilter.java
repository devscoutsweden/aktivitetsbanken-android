package se.devscout.android.model.repo;

import se.devscout.android.util.IsFeaturedFilter;
import se.devscout.server.api.model.ActivityProperties;

public class SQLIsFeaturedFilter extends IsFeaturedFilter implements SQLActivityFilter {
    @Override
    public boolean matches(ActivityProperties properties) {
        return true;
    }

    @Override
    public void applyFilter(SQLQueryBuilder queryBuilder) {
        queryBuilder.addWhereIsFeatured(true);
    }
}
