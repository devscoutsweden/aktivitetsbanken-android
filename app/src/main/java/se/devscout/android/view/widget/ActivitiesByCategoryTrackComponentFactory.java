package se.devscout.android.view.widget;

import android.support.v4.app.Fragment;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.CategoryTrackListFragment;

class ActivitiesByCategoryTrackComponentFactory extends AbstractComponentFactory implements TabComponentFactory{
    public ActivitiesByCategoryTrackComponentFactory(String name) {
        super(name, R.string.startTabTracks, R.drawable.ic_action_labels);
    }

    @Override
    public Fragment createFragment() {
        return CategoryTrackListFragment.create();
    }
}
