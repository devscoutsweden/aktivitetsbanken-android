package se.devscout.android.view.widget;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.controller.fragment.ActivitiesListFragment;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.controller.fragment.FavouriteActivitiesListFragment;
import se.devscout.android.view.AbstractActivitiesFinderComponentFactory;
import se.devscout.android.view.FavouriteActivitiesListView;

public class FavouriteActivitiesFinderComponentFactory extends AbstractActivitiesFinderComponentFactory {
    public FavouriteActivitiesFinderComponentFactory(int nameResId, int iconResId) {
        super(iconResId, nameResId, true, true, true);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, ActivityBankFragment activityBankFragment) {

        FavouriteActivitiesListView mView = new FavouriteActivitiesListView(activityBankFragment, true);

        // Start search in separate thread
        Log.d(ActivitiesListFragment.class.getName(), "Result has not been returned/cached. Starting search task in new thread.");

        mView.runSearchTaskInNewThread();

        return mView;
    }

    @Override
    public Fragment createFragment() {
        return FavouriteActivitiesListFragment.create();
    }
}