package se.devscout.android.view;

import android.support.v4.app.Fragment;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.SearchHistoryListFragment;

class SearchHistoryComponentFactory extends AbstractActivitiesFinderComponentFactory  implements TabComponentFactory{

    public SearchHistoryComponentFactory(String name) {
        super(name, R.string.drawer_search_history_header, R.drawable.ic_action_time, false);
    }

    @Override
    public Fragment createFragment() {
        return SearchHistoryListFragment.create();
    }
}
