package se.devscout.android.view.widget;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.controller.fragment.FavouriteActivitiesListFragment;
import se.devscout.android.view.AbstractActivitiesFinderComponentFactory;
import se.devscout.android.view.FavouriteActivitiesListView;
import se.devscout.android.view.TabComponentFactory;
import se.devscout.android.view.WidgetComponentFactory;

public class FavouriteActivitiesFinderComponentFactory extends AbstractActivitiesFinderComponentFactory  implements TabComponentFactory, WidgetComponentFactory {
    public FavouriteActivitiesFinderComponentFactory(String name) {
        super(name, R.drawable.ic_action_important, R.string.startTabFavourites, true, true, true);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, ActivityBankFragment activityBankFragment) {

        FavouriteActivitiesListView mView = new FavouriteActivitiesListView(activityBankFragment, true);

        mView.runSearchTaskInNewThread();

        return mView;
    }

    @Override
    public Fragment createFragment() {
        return FavouriteActivitiesListFragment.create();
    }
}
