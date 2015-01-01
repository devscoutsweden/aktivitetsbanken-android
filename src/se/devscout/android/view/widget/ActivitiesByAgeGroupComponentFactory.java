package se.devscout.android.view.widget;

import android.support.v4.app.Fragment;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.AgeGroupListFragment;

class ActivitiesByAgeGroupComponentFactory extends AbstractComponentFactory implements TabComponentFactory {

    public ActivitiesByAgeGroupComponentFactory(String name) {
        super(name, R.string.startTabAgeGroups, R.drawable.ic_action_cc_bcc);
    }

    @Override
    public Fragment createFragment() {
        return AgeGroupListFragment.create();
    }
}
