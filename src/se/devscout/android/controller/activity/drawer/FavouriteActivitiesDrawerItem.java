package se.devscout.android.controller.activity.drawer;

import android.support.v4.app.Fragment;
import se.devscout.android.controller.fragment.FavouriteActivitiesListFragment;

public class FavouriteActivitiesDrawerItem extends ExecutableDrawerItem {
    public FavouriteActivitiesDrawerItem(String titleResId, int iconResIdesId) {
        super(titleResId, iconResIdesId);
    }

    public Fragment createFragment() {
        return FavouriteActivitiesListFragment.create();
    }
}
