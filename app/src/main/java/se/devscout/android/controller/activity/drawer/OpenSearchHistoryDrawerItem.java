package se.devscout.android.controller.activity.drawer;

import android.support.v4.app.Fragment;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.SearchHistoryListFragment;

class OpenSearchHistoryDrawerItem extends ExecutableDrawerItem {

    private final String mActivityTitle;

    public OpenSearchHistoryDrawerItem(String title, String activityTitle) {
        super(title, R.drawable.ic_action_overflow);
        mActivityTitle = activityTitle;
    }

    @Override
    public boolean isHeader() {
        return false;
    }

    @Override
    public Fragment createFragment() {
        return SearchHistoryListFragment.create();
    }

    @Override
    protected String getActivityTitle() {
        return mActivityTitle;
    }
}
