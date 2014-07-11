package se.devscout.android.view.widget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.controller.activity.FragmentCreator;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.controller.fragment.FeaturedActivitiesListFragment;
import se.devscout.android.view.AbstractActivitiesFinder;
import se.devscout.android.view.FeaturedActivitiesListView;

public class FeaturedActivitiesFinder extends AbstractActivitiesFinder implements WidgetSpecification, FragmentCreator {

    public FeaturedActivitiesFinder(int nameResId, int iconResId) {
        super(iconResId, nameResId);
    }

    @Override
    public boolean isTitleImportant() {
        return true;
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, ActivityBankFragment activityBankFragment) {

        FeaturedActivitiesListView mView = new FeaturedActivitiesListView(activityBankFragment, true);

        // Start search in separate thread
        mView.runSearchTaskInNewThread();

        return mView;
    }

    @Override
    public boolean isDefaultWidget() {
        return true;
    }

    @Override
    public Fragment createFragment() {
        return FeaturedActivitiesListFragment.create();
    }

    @Override
    public boolean isDefaultTab() {
        return false;
    }
}
