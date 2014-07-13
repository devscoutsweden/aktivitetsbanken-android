package se.devscout.android.model.repo.sql;

import se.devscout.android.model.IntegerRangePojo;
import se.devscout.android.util.SimpleTimeRangeFilter;
import se.devscout.server.api.model.Range;

public class SQLTimeRangeFilter extends SimpleTimeRangeFilter implements SQLActivityFilter {
    public SQLTimeRangeFilter(Range<Integer> range) {
        super(range);
    }

    @Override
    public void applyFilter(QueryBuilder queryBuilder) {
        queryBuilder.addWhereTime(new IntegerRangePojo(getMin(), getMax()));
    }
}
