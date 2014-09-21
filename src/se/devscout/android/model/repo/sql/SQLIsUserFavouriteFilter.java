package se.devscout.android.model.repo.sql;

import android.net.Uri;
import se.devscout.android.util.SimpleFilter;
import se.devscout.server.api.ActivityFilterVisitor;
import se.devscout.server.api.URIBuilderActivityFilterVisitor;
import se.devscout.server.api.activityfilter.IsUserFavouriteFilter;
import se.devscout.server.api.model.ActivityProperties;
import se.devscout.server.api.model.UserKey;

class SQLIsUserFavouriteFilter extends SimpleFilter implements IsUserFavouriteFilter, SQLActivityFilter {
    private UserKey mUserKey;

    public SQLIsUserFavouriteFilter(UserKey userKey) {
        mUserKey = userKey;
    }

    @Override
    public UserKey getUserKey() {
        return mUserKey;
    }

    @Override
    public boolean matches(ActivityProperties properties) {
        return true;
    }

    @Override
    public void applyFilter(QueryBuilder queryBuilder) {
        queryBuilder.addWhereFavourite(mUserKey);
    }

    @Override
    public String toString(ActivityFilterVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Uri toAPIRequest(URIBuilderActivityFilterVisitor visitor) {
        return visitor.visit(this);
    }
}
