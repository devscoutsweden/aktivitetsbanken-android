package se.devscout.android.model.repo.remote;

import android.net.Uri;
import android.text.TextUtils;
import se.devscout.android.util.LogUtil;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.AverageRatingFilter;
import se.devscout.server.api.RelatedToFilter;
import se.devscout.server.api.URIBuilderActivityFilterVisitor;
import se.devscout.server.api.activityfilter.*;

public class ApiV1Visitor implements URIBuilderActivityFilterVisitor {

    private final Uri.Builder mUriBuilder;

    public ApiV1Visitor(String host) {
        //TODO: the host to use should be a configuration parameter, and preferable something that can change with time without having to redistribute the app.
        mUriBuilder = Uri.parse(host).buildUpon();
    }

    @Override
    public Uri visit(AndFilter filter) {
        return visitCompound(filter);
    }

    private Uri visitCompound(CompoundFilter filter) {
        for (ActivityFilter subFilter : filter.getFilters()) {
            subFilter.visit(this);
        }
        return mUriBuilder.build();
    }

    @Override
    public Uri visit(CategoryFilter filter) {
        mUriBuilder.appendQueryParameter("categories", String.valueOf(filter.getServerId()));
        return mUriBuilder.build();
    }

    @Override
    public Uri visit(IsUserFavouriteFilter filter) {
        if (filter.getUserKey() != null) {
            LogUtil.i(ApiV1Visitor.class.getName(), "Cannot handle specific users. UserKey should be null.");
        }
        mUriBuilder.appendQueryParameter("my_favourites", "true");
        return mUriBuilder.build();
    }

    @Override
    public Uri visit(TimeRangeFilter filter) {
        appendRangeFilter(filter, "time");
        return mUriBuilder.build();
    }

    @Override
    public Uri visit(AgeRangeFilter filter) {
        appendRangeFilter(filter, "age");
        return mUriBuilder.build();
    }

    private void appendRangeFilter(RangeFilter filter, final String field) {
        if (filter.getMin() < Integer.MAX_VALUE && filter.getMin() >= 0) {
            mUriBuilder.appendQueryParameter(field + "_1", filter.getMin().toString());
        }
        if (filter.getMax() < Integer.MAX_VALUE && filter.getMax() >= 0) {
            mUriBuilder.appendQueryParameter(field + "_2", filter.getMax().toString());
        }
    }

    @Override
    public Uri visit(IsFeaturedFilter filter) {
        mUriBuilder.appendQueryParameter("featured", Boolean.TRUE.toString());
        return mUriBuilder.build();
    }

    @Override
    public Uri visit(TextFilter filter) {
        mUriBuilder.appendQueryParameter("text", filter.getCondition());
        return mUriBuilder.build();
    }

    @Override
    public Uri visit(ActivityKeysFilter filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uri visit(RandomActivitiesFilter filter) {
        mUriBuilder.appendQueryParameter("random", String.valueOf(filter.getNumberOfActivities()));
        return mUriBuilder.build();
    }

    @Override
    public Uri visit(ServerObjectIdentifiersFilter filter) {
        mUriBuilder.appendQueryParameter("id", TextUtils.join(",", filter.getIdentifiers()));
        return mUriBuilder.build();
    }

    @Override
    public Uri visit(OverallFavouriteActivitiesFilter filter) {
        mUriBuilder.appendQueryParameter("favourites", String.valueOf(filter.getNumberOfActivities()));
        return mUriBuilder.build();
    }

    @Override
    public Uri visit(AverageRatingFilter filter) {
        mUriBuilder.appendQueryParameter("ratings_average_min", String.valueOf(filter.getLimit()));
        return mUriBuilder.build();
    }

    @Override
    public Uri visit(RelatedToFilter filter) {
        // The server API does not support searching for activities related to another activity
        throw new UnsupportedOperationException();
    }
}
