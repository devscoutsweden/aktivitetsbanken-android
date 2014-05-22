package se.devscout.android.controller.activity.drawer;

import android.support.v4.app.Fragment;
import se.devscout.android.controller.fragment.CategoriesListFragment;

public class CategoriesDrawerItem extends ExecutableDrawerItem {
    public CategoriesDrawerItem(String titleResId, int iconResIdesId) {
        super(titleResId, iconResIdesId);
    }

    public Fragment createFragment() {
        return CategoriesListFragment.create();
    }
}
