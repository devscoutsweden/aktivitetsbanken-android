package se.devscout.android.view;

import android.support.v4.app.Fragment;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.AgeGroupListFragment;

class ByAgeGroupComponentFactory extends AbstractActivitiesFinderComponentFactory implements TabComponentFactory {

    public ByAgeGroupComponentFactory(String name) {
        super(name, R.string.startTabAgeGroups, R.drawable.ic_action_cc_bcc, false);
    }

    @Override
    public Fragment createFragment() {
        return AgeGroupListFragment.create();
    }
}
