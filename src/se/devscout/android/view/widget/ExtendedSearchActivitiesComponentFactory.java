package se.devscout.android.view.widget;

import android.support.v4.app.Fragment;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.SearchFragment;

class ExtendedSearchActivitiesComponentFactory extends AbstractComponentFactory implements TabComponentFactory{
    public ExtendedSearchActivitiesComponentFactory(String name) {
        super(name, R.string.startTabSearch, R.drawable.ic_action_search);
    }

    @Override
    public Fragment createFragment() {
        return SearchFragment.create();
    }
}
