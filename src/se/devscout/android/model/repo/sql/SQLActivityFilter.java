package se.devscout.android.model.repo.sql;

public interface SQLActivityFilter {
    void applyFilter(SQLQueryBuilder queryBuilder);
}
