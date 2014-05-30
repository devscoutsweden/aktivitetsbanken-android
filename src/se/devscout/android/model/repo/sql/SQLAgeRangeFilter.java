package se.devscout.android.model.repo.sql;

import se.devscout.android.model.IntegerRangePojo;
import se.devscout.android.util.SimpleAgeRangeFilter;
import se.devscout.server.api.model.Range;

public class SQLAgeRangeFilter extends SimpleAgeRangeFilter implements SQLActivityFilter {
    public SQLAgeRangeFilter(Range<Integer> range) {
        super(range);
    }

    @Override
    public void applyFilter(QueryBuilder queryBuilder) {
        queryBuilder.addWhereAge(new IntegerRangePojo(getMin(), getMax()));
    }
}
