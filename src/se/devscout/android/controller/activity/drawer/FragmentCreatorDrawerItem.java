package se.devscout.android.controller.activity.drawer;

import android.support.v4.app.Fragment;
import se.devscout.android.view.AbstractActivitiesFinderComponentFactory;

public class FragmentCreatorDrawerItem extends ExecutableDrawerItem {
    private final AbstractActivitiesFinderComponentFactory mCreator;

    public FragmentCreatorDrawerItem(String title, int iconResIdesId, AbstractActivitiesFinderComponentFactory creator) {
        super(title, iconResIdesId);
        mCreator = creator;
    }

    public Fragment createFragment() {
        return mCreator.createFragment();
    }
}
