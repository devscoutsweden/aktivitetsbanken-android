package se.devscout.android.controller.activity.drawer;

import android.support.v4.app.Fragment;
import se.devscout.android.controller.fragment.AgeGroupListFragment;

public class AgeGroupsDrawerItem extends ExecutableDrawerItem {
    public AgeGroupsDrawerItem(String titleResId, int iconResIdesId) {
        super(titleResId, iconResIdesId);
    }

    public Fragment createFragment() {
        return AgeGroupListFragment.create();
    }
}
