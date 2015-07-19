package se.devscout.android.view.widget;

import android.support.v4.app.Fragment;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.SearchHistoryListFragment;

class SearchHistoryComponentFactory extends AbstractComponentFactory implements TabComponentFactory{

    public SearchHistoryComponentFactory(String name) {
        super(name, R.string.drawer_search_history_header, R.drawable.ic_action_time);
    }

    @Override
    public Fragment createFragment() {
        return SearchHistoryListFragment.create();
    }
}
