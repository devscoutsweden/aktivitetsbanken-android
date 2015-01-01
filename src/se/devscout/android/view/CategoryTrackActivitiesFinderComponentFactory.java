package se.devscout.android.view;

import android.support.v4.app.Fragment;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.CategoryTrackListFragment;

class CategoryTrackActivitiesFinderComponentFactory extends AbstractActivitiesFinderComponentFactory  implements TabComponentFactory{
    public CategoryTrackActivitiesFinderComponentFactory(String name) {
        super(name, R.string.startTabTracks, R.drawable.ic_action_labels, false);
    }

    @Override
    public Fragment createFragment() {
        return CategoryTrackListFragment.create();
    }
}
