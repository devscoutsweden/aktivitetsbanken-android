package se.devscout.android.view.widget;

import android.support.v4.app.Fragment;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.HomeWidgetFragment;

class HomeComponentFactory extends AbstractComponentFactory implements TabComponentFactory {
    public HomeComponentFactory(String name) {
        super(name, R.string.startTabHome, R.drawable.ic_action_storage);
    }

    @Override
    public Fragment createFragment() {
        return HomeWidgetFragment.create();
    }
}
