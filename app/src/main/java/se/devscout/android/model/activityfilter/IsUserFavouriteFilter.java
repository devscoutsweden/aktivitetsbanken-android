package se.devscout.android.model.activityfilter;

import se.devscout.android.model.UserKey;

public interface IsUserFavouriteFilter extends ActivityFilter {
    UserKey getUserKey();
}
