package se.devscout.server.api.activityfilter;

import se.devscout.android.model.UserKey;

public interface IsUserFavouriteFilter extends ActivityFilter {
    UserKey getUserKey();
}
