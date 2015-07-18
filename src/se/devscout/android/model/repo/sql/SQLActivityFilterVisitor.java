package se.devscout.android.model.repo.sql;

import se.devscout.android.model.IntegerRange;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.BaseActivityFilterVisitor;
import se.devscout.server.api.activityfilter.*;

class SQLActivityFilterVisitor implements BaseActivityFilterVisitor<Void> {
    private final QueryBuilder mQueryBuilder;

    public SQLActivityFilterVisitor(QueryBuilder queryBuilder) {
        mQueryBuilder = queryBuilder;
    }

    @Override
    public Void visit(AndFilter filter) {
        for (ActivityFilter subFilter : filter.getFilters()) {
            subFilter.visit(this);
        }
        return null;
    }

    @Override
    public Void visit(CategoryFilter filter) {
        mQueryBuilder.addWhereCategory(filter);
        return null;
    }

    @Override
    public Void visit(IsUserFavouriteFilter filter) {
        mQueryBuilder.addWhereFavourite(filter.getUserKey());
        return null;
    }

    @Override
    public Void visit(TimeRangeFilter filter) {
        mQueryBuilder.addWhereTime(new IntegerRange(filter.getMin(), filter.getMax()));
        return null;
    }

    @Override
    public Void visit(AgeRangeFilter filter) {
        mQueryBuilder.addWhereAge(new IntegerRange(filter.getMin(), filter.getMax()));
        return null;
    }

    @Override
    public Void visit(IsFeaturedFilter filter) {
        mQueryBuilder.addWhereIsFeatured();
        return null;
    }

    @Override
    public Void visit(TextFilter filter) {
        mQueryBuilder.addWhereText(filter.getCondition());
        return null;
    }

    @Override
    public Void visit(ActivityKeysFilter filter) {
        mQueryBuilder.addWhereActivities(filter.getActivityKeys());
        return null;
    }

    @Override
    public Void visit(RandomActivitiesFilter filter) {
        mQueryBuilder.addRandomlySelectedRows(filter.getNumberOfActivities());
        return null;
    }

    @Override
    public Void visit(ServerObjectIdentifiersFilter filter) {
        throw new UnsupportedOperationException("Cannot search for activities based on server-side identifiers in local database.");
    }

    @Override
    public Void visit(OverallFavouriteActivitiesFilter filter) {
        mQueryBuilder.addWhereOverallFavourites(filter);
        return null;
    }

    @Override
    public Void visit(AverageRatingFilter filter) {
        mQueryBuilder.addWhereAverageRating(filter);
        return null;
    }

    @Override
    public Void visit(RelatedToFilter filter) {
        mQueryBuilder.addWhereRelatedTo(filter);
        return null;
    }
}
