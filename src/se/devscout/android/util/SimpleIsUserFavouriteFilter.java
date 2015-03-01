package se.devscout.android.util;

import se.devscout.server.api.BaseActivityFilterVisitor;
import se.devscout.server.api.activityfilter.IsUserFavouriteFilter;
import se.devscout.server.api.model.UserKey;

class SimpleIsUserFavouriteFilter implements IsUserFavouriteFilter {
    private UserKey mUserKey;

    public SimpleIsUserFavouriteFilter(UserKey userKey) {
        mUserKey = userKey;
    }

    @Override
    public UserKey getUserKey() {
        return mUserKey;
    }

    @Override
    public <T> T visit(BaseActivityFilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
