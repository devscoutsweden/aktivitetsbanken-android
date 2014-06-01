package se.devscout.android.model.repo;

import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.ActivityFilterVisitor;
import se.devscout.server.api.activityfilter.*;

class EqualsFilterVisitor implements ActivityFilterVisitor {
    @Override
    public String visit(OrFilter filter) {
        return visitCompound(filter, '|');
    }

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
            sb.append(subFilter.toString(this));
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
    public String visit(ActivityKeyFilter filter) {
        return "activity" + filter.getActivityKey().getId();
    }
}
