package se.devscout.android.controller.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.view.FeaturedActivitiesListView;

public class FeaturedWidget implements StartScreenWidget {
    private FeaturedActivitiesListView mView;

    public FeaturedWidget() {
    }


    @Override
    public int getTitleResId() {
        return R.string.startTabFeatured;
    }

    @Override
    public View[] getViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = new FeaturedActivitiesListView(container.getContext(), true);

        // Start search in separate thread
        mView.createSearchTask().execute();

        return new View[]{mView};
    }

    @Override
    public void onFragmentResume() {
    }
}
