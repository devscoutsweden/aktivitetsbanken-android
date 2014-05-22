package se.devscout.android.controller.activity.drawer;

import android.support.v4.app.Fragment;
import se.devscout.android.controller.fragment.SearchFragment;

public class SearchDrawerItem extends ExecutableDrawerItem {
    public SearchDrawerItem(String titleResId, int iconResIdesId) {
        super(titleResId, iconResIdesId);
    }

    public Fragment createFragment() {
        return SearchFragment.create();
    }
}
