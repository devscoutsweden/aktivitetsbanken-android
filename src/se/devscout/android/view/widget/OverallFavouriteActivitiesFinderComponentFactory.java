package se.devscout.android.view.widget;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.controller.fragment.OverallFavouriteActivitiesListFragment;
import se.devscout.android.view.AbstractActivitiesFinderComponentFactory;
import se.devscout.android.view.OverallFavouriteActivitiesListView;

public class OverallFavouriteActivitiesFinderComponentFactory extends AbstractActivitiesFinderComponentFactory {
    public OverallFavouriteActivitiesFinderComponentFactory(int nameResId, int iconResId) {
        super(iconResId, nameResId, false, true, true);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, ActivityBankFragment activityBankFragment) {

        OverallFavouriteActivitiesListView mView = new OverallFavouriteActivitiesListView(activityBankFragment, true);

        mView.runSearchTaskInNewThread();

        return mView;
    }

    @Override
    public Fragment createFragment() {
        return OverallFavouriteActivitiesListFragment.create();
    }
}
