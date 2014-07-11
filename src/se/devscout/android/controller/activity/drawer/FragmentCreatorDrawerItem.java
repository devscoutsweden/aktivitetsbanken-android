package se.devscout.android.controller.activity.drawer;

import android.support.v4.app.Fragment;
import se.devscout.android.controller.activity.FragmentCreator;

public class FragmentCreatorDrawerItem extends ExecutableDrawerItem {
    private final FragmentCreator mCreator;

    public FragmentCreatorDrawerItem(String titleResId, int iconResIdesId, FragmentCreator creator) {
        super(titleResId, iconResIdesId);
        mCreator = creator;
    }

    public Fragment createFragment() {
        return mCreator.createFragment();
    }
}
