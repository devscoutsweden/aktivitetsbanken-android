package se.devscout.android.controller.activity.drawer;

import android.support.v4.app.Fragment;
import se.devscout.android.controller.fragment.ActivitiesListFragment;
import se.devscout.android.view.ActivitiesListView;
import se.devscout.server.api.ActivityFilter;

public class ActivitiesListDrawerItem extends ExecutableDrawerItem {
    private ActivityFilter mFilter;

    public ActivitiesListDrawerItem(String title, int iconResIdesId, ActivityFilter filter) {
        super(title, iconResIdesId);
        mFilter = filter;
    }

    @Override
    public Fragment createFragment() {
        return ActivitiesListFragment.create(mFilter, ActivitiesListView.Sorter.NAME);
    }
}
