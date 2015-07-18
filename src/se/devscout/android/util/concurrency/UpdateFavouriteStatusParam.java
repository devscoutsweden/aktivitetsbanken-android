package se.devscout.android.util.concurrency;

import se.devscout.android.model.ActivityKey;

public class UpdateFavouriteStatusParam {
    private final ActivityKey mActivityKey;
    private final boolean mToBeSetAsFavourite;

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
