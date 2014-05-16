package se.devscout.android.model.repo;

import se.devscout.android.util.PrimitiveFilter;
import se.devscout.server.api.activityfilter.IsUserFavouriteFilter;
import se.devscout.server.api.model.ActivityProperties;
import se.devscout.server.api.model.UserKey;

class SQLIsUserFavouriteFilter extends PrimitiveFilter implements IsUserFavouriteFilter, SQLActivityFilter {
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
    public void applyFilter(SQLQueryBuilder queryBuilder) {
        queryBuilder.addWhereFavourite(mUserKey);
    }
}