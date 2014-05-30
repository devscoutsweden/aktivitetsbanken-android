package se.devscout.android.controller.fragment;

import android.content.Context;
import se.devscout.android.R;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.ActivityFilterVisitor;
import se.devscout.server.api.activityfilter.*;

public class SubtitleActivityFilterVisitor implements ActivityFilterVisitor {
    private Context mContext;

    public SubtitleActivityFilterVisitor(Context context) {
        mContext = context;
    }

    @Override
    public String visit(OrFilter filter) {
        return visitCompound(filter, mContext.getString(R.string.subtitle_filter_visitor_or));
    }

    @Override
    public String visit(AndFilter filter) {
        return visitCompound(filter, mContext.getString(R.string.subtitle_filter_visitor_and));
    }

    private String visitCompound(CompoundFilter filter, String separator) {
        StringBuilder sb = new StringBuilder();
        for (ActivityFilter subFilter : filter.getFilters()) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append(subFilter.toString(this));
        }
        return sb.toString();
    }

    @Override
    public String visit(CategoryFilter filter) {
        return mContext.getString(R.string.subtitle_filter_visitor_incategory, filter.getName());
    }

    @Override
    public String visit(IsUserFavouriteFilter filter) {
        return mContext.getString(R.string.subtitle_filter_visitor_favourite);
    }

    @Override
    public String visit(TimeRangeFilter filter) {
        if (filter.getMin() > 0 && filter.getMax() > 0) {
            return mContext.getString(R.string.subtitle_filter_visitor_time_range, filter.getMin(), filter.getMax());
        } else if (filter.getMin() > 0) {
            return mContext.getString(R.string.subtitle_filter_visitor_time_older, filter.getMin());
        } else {
            return mContext.getString(R.string.subtitle_filter_visitor_time_younger, filter.getMax());
        }
    }

    @Override
    public String visit(AgeRangeFilter filter) {
        if (filter.getMin() > 0 && filter.getMax() > 0) {
            return mContext.getString(R.string.subtitle_filter_visitor_age_range, filter.getMin(), filter.getMax());
        } else if (filter.getMin() > 0) {
            return mContext.getString(R.string.subtitle_filter_visitor_age_older, filter.getMin());
        } else {
            return mContext.getString(R.string.subtitle_filter_visitor_age_younger, filter.getMax());
        }
    }

    @Override
    public String visit(IsFeaturedFilter filter) {
        return mContext.getString(R.string.subtitle_filter_visitor_featured);
    }

    @Override
    public String visit(TextFilter filter) {
        return filter.getCondition();
    }
}