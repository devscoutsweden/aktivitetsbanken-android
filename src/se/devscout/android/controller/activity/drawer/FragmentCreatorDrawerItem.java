package se.devscout.android.controller.activity.drawer;

import android.support.v4.app.Fragment;
import se.devscout.android.view.TabComponentFactory;

public class FragmentCreatorDrawerItem extends ExecutableDrawerItem {
    private final TabComponentFactory mCreator;

    public FragmentCreatorDrawerItem(String title, int iconResIdesId, TabComponentFactory creator) {
        super(title, iconResIdesId);
        mCreator = creator;
    }

    public Fragment createFragment() {
        return mCreator.createFragment();
    }
}
