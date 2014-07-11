package se.devscout.android.view.widget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.controller.fragment.FeaturedActivitiesListFragment;
import se.devscout.android.view.AbstractActivitiesFinderComponentFactory;
import se.devscout.android.view.FeaturedActivitiesListView;

public class FeaturedActivitiesFinderComponentFactory extends AbstractActivitiesFinderComponentFactory {

    public FeaturedActivitiesFinderComponentFactory(int nameResId, int iconResId) {
        super(iconResId, nameResId, false, true, true);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, ActivityBankFragment activityBankFragment) {

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
