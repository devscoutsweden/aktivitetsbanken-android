package se.devscout.android.view.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.view.FeaturedActivitiesListView;

public class FeaturedWidgetSpecification implements WidgetSpecification {
    private FeaturedActivitiesListView mView;

    public FeaturedWidgetSpecification() {
    }


    @Override
    public int getTitleResId() {
        return R.string.startTabFeatured;
    }

    @Override
    public View[] getViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, ActivityBankFragment activityBankFragment) {

        mView = new FeaturedActivitiesListView(container.getContext(), true);

        // Start search in separate thread
        mView.runSearchTaskInNewThread();

        return new View[]{mView};
    }
}
