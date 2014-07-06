package se.devscout.android.view.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.view.CategoriesListSearchView;

public class CategoriesWidgetSpecification implements WidgetSpecification {
    private CategoriesListSearchView mView;

    public CategoriesWidgetSpecification() {
    }


    @Override
    public int getTitleResId() {
        return R.string.startTabCategories;
    }

    @Override
    public View[] getViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, ActivityBankFragment activityBankFragment) {

        mView = new CategoriesListSearchView(container.getContext(), R.string.searchResultEmptyMessage,R.string.searchResultEmptyTitle, true);

        // Start search in separate thread
        mView.runSearchTaskInNewThread();

        return new View[]{mView};
    }
}
