package se.devscout.android.controller.activity.drawer;

import android.support.v4.app.Fragment;
import se.devscout.android.controller.fragment.HomeFragment;

public class StartDrawerItem extends ExecutableDrawerItem {
    public StartDrawerItem(String titleResId, int iconResIdesId) {
        super(titleResId, iconResIdesId);
    }

    public Fragment createFragment() {
        return HomeFragment.create();
    }
}
