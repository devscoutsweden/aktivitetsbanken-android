package se.devscout.android.view.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.view.CategoriesListSearchView;

public class CategoriesWidget implements StartScreenWidget {
    private CategoriesListSearchView mView;

    public CategoriesWidget() {
    }


    @Override
    public int getTitleResId() {
        return R.string.startTabCategories;
    }

    @Override
    public View[] getViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = new CategoriesListSearchView(container.getContext(), R.string.searchResultEmptyMessage,R.string.searchResultEmptyTitle, true);

        // Start search in separate thread
        mView.createSearchTask().execute();

        return new View[]{mView};
    }

    @Override
    public void onFragmentResume() {
    }
}
