package se.devscout.android.model;

import android.text.TextUtils;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.ActivityFilterVisitor;
import se.devscout.server.api.AverageRatingFilter;
import se.devscout.server.api.activityfilter.*;

class EqualsFilterVisitor implements ActivityFilterVisitor {

    @Override
    public String visit(AndFilter filter) {
        return visitCompound(filter, '&');
    }

    private String visitCompound(CompoundFilter filter, char separator) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (ActivityFilter subFilter : filter.getFilters()) {
            if (sb.length() > 1) {
                sb.append(separator);
            }
            sb.append(subFilter.visit(this));
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public String visit(CategoryFilter filter) {
        return "cat" + filter.getGroup() + "," + filter.getName();
    }

    @Override
    public String visit(IsUserFavouriteFilter filter) {
        return "fav" + filter.getUserKey().getId();
    }

    @Override
    public String visit(TimeRangeFilter filter) {
        return "time" + filter.getMin() + "-" + filter.getMax();
    }

    @Override
    public String visit(AgeRangeFilter filter) {
        return "age" + filter.getMin() + "-" + filter.getMax();
    }

    @Override
    public String visit(IsFeaturedFilter filter) {
        return "featured";
    }

    @Override
    public String visit(TextFilter filter) {
        return "text" + filter.getCondition();
    }

    @Override
    public String visit(ActivityKeysFilter filter) {
        return "activity" + TextUtils.join(",", filter.getActivityKeys());
    }

    @Override
    public String visit(RandomActivitiesFilter filter) {
        return "random" + filter.getNumberOfActivities();
    }

    @Override
    public String visit(ServerObjectIdentifiersFilter filter) {
        return "serverid" + TextUtils.join(",", filter.getIdentifiers());
    }

    @Override
    public String visit(OverallFavouriteActivitiesFilter filter) {
        return "overallfavourite" + filter.getNumberOfActivities();
    }

    @Override
    public String visit(AverageRatingFilter filter) {
        return "averagerating" + filter.getLimit();
    }
}
