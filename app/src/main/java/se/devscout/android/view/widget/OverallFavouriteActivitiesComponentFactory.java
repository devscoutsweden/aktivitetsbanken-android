package se.devscout.android.view.widget;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.controller.fragment.OverallFavouriteActivitiesListFragment;
import se.devscout.android.view.OverallFavouriteActivitiesListView;

public class OverallFavouriteActivitiesComponentFactory extends AbstractComponentFactory implements TabComponentFactory, WidgetComponentFactory {
    public OverallFavouriteActivitiesComponentFactory(String name) {
        super(name, R.string.startTabOverallFavourites, R.drawable.ic_action_important, true);
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
