package se.devscout.android.util;

import se.devscout.android.model.UserKey;
import se.devscout.server.api.activityfilter.BaseActivityFilterVisitor;
import se.devscout.server.api.activityfilter.IsUserFavouriteFilter;

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
