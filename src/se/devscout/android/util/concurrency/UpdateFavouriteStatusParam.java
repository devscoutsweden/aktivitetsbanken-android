package se.devscout.android.util.concurrency;

import se.devscout.server.api.model.ActivityKey;

public class UpdateFavouriteStatusParam {
    private ActivityKey mActivityKey;
    private boolean mToBeSetAsFavourite;

    public UpdateFavouriteStatusParam(ActivityKey activityKey, boolean toBeSetAsFavourite) {
        mActivityKey = activityKey;
        mToBeSetAsFavourite = toBeSetAsFavourite;
    }

    public ActivityKey getActivityKey() {
        return mActivityKey;
    }

    public boolean isToBeSetAsFavourite() {
        return mToBeSetAsFavourite;
    }
}
