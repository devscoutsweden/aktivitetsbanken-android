package se.devscout.android.view.widget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.controller.fragment.CategoriesListFragment;
import se.devscout.android.view.AbstractActivitiesFinderComponentFactory;
import se.devscout.android.view.CategoriesListSearchView;

public class ByCategoryActivitiesFinderComponentFactory extends AbstractActivitiesFinderComponentFactory {
    public ByCategoryActivitiesFinderComponentFactory(int nameResId, int iconResId) {
        super(iconResId, nameResId, true, false, false);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, ActivityBankFragment activityBankFragment) {

        CategoriesListSearchView mView = new CategoriesListSearchView(container.getContext(), R.string.searchResultEmptyMessage,R.string.searchResultEmptyTitle, true);

        // Start search in separate thread
        mView.runSearchTaskInNewThread();

        return mView;
    }

    @Override
    public Fragment createFragment() {
        return CategoriesListFragment.create();
    }
}
