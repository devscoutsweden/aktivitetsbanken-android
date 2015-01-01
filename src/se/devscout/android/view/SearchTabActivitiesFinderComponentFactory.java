package se.devscout.android.view;

import android.support.v4.app.Fragment;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.SearchFragment;

class SearchTabActivitiesFinderComponentFactory extends AbstractActivitiesFinderComponentFactory  implements TabComponentFactory{
    public SearchTabActivitiesFinderComponentFactory(String name) {
        super(name, R.string.startTabSearch, R.drawable.ic_action_search, true);
    }

    @Override
    public Fragment createFragment() {
        return SearchFragment.create();
    }
}
