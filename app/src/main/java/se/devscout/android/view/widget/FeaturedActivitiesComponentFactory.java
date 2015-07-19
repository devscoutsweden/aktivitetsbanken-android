package se.devscout.android.view.widget;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.controller.fragment.FeaturedActivitiesListFragment;
import se.devscout.android.view.FeaturedActivitiesListView;

public class FeaturedActivitiesComponentFactory extends AbstractComponentFactory implements TabComponentFactory, WidgetComponentFactory {

    public FeaturedActivitiesComponentFactory(String name) {
        super(name, R.string.startTabFeatured, R.drawable.ic_action_good, true);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, ActivityBankFragment activityBankFragment) {

        FeaturedActivitiesListView mView = new FeaturedActivitiesListView(activityBankFragment, true);

        // Start search in separate thread
        mView.runSearchTaskInNewThread();

        return mView;
    }

    @Override
    public Fragment createFragment() {
        return FeaturedActivitiesListFragment.create();
    }
}
