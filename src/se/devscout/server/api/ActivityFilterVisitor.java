package se.devscout.server.api;

import se.devscout.android.model.repo.sql.SQLRandomActivitiesFilter;
import se.devscout.server.api.activityfilter.*;

public interface ActivityFilterVisitor {
    String visit(OrFilter filter);

    String visit(AndFilter filter);

    String visit(CategoryFilter filter);

    String visit(IsUserFavouriteFilter filter);

    String visit(TimeRangeFilter filter);

    String visit(AgeRangeFilter filter);

    String visit(IsFeaturedFilter filter);

    String visit(TextFilter filter);

    String visit(ActivityKeyFilter filter);

    String visit(SQLRandomActivitiesFilter filter);
}
