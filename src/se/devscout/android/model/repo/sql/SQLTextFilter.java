package se.devscout.android.model.repo.sql;

import se.devscout.android.util.SimpleTextFilter;
import se.devscout.server.api.model.ActivityProperties;

public class SQLTextFilter extends SimpleTextFilter implements SQLActivityFilter {
    public SQLTextFilter(String condition) {
        super(condition);
    }

    @Override
    public void applyFilter(QueryBuilder queryBuilder) {
        queryBuilder.addWhereText(getCondition());
    }

    @Override
    public boolean matches(ActivityProperties properties) {
        return true;
    }
}
