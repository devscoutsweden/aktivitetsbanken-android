package se.devscout.server.api.activityfilter;

import se.devscout.server.api.model.UserKey;

public interface IsUserFavouriteFilter extends ActivityFilter {
    UserKey getUserKey();
}
