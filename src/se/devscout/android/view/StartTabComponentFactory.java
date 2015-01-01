package se.devscout.android.view;

import android.support.v4.app.Fragment;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.StartWidgetFragment;

class StartTabComponentFactory extends AbstractActivitiesFinderComponentFactory implements TabComponentFactory {
    public StartTabComponentFactory(String name) {
        super(name, R.string.startTabHome, R.drawable.ic_action_storage, true);
    }

    @Override
    public Fragment createFragment() {
        return StartWidgetFragment.create();
    }
}
