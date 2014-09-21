package se.devscout.server.api;

import se.devscout.android.model.repo.sql.SQLRandomActivitiesFilter;
import se.devscout.server.api.activityfilter.*;

public interface BaseActivityFilterVisitor<T> {
    T visit(OrFilter filter);

    T visit(AndFilter filter);

    T visit(CategoryFilter filter);

    T visit(IsUserFavouriteFilter filter);

    T visit(TimeRangeFilter filter);

    T visit(AgeRangeFilter filter);

    T visit(IsFeaturedFilter filter);

    T visit(TextFilter filter);

    T visit(ActivityKeyFilter filter);

    T visit(SQLRandomActivitiesFilter filter);
}
