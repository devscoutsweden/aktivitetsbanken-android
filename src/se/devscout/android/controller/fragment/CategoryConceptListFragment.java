package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import se.devscout.android.CategoryConcept;
import se.devscout.android.view.CategoryConceptListView;

public class CategoryConceptListFragment extends QuickSearchListFragment<CategoryConcept, CategoryConceptListView> {

    public CategoryConceptListFragment() {
        super(CategoryConcept.values());
    }

/*
    @Override
    protected int getImageResId(CategoryConcept item) {
        return item.getLogoResId();
    }

    @Override
    protected String getTitle(CategoryConcept option) {
        return getString(option.getTitleResId());
    }

    @Override
    protected String getSubtitle(CategoryConcept option) {
        return null;
    }

    @Override
    protected ActivityFilter createFilter(CategoryConcept option) {
        return new SimpleCategoryFilter(option.getScoutCategoryConcept());
    }

    @Override
    protected String getSearchResultTitle(CategoryConcept option) {
        return getString(R.string.searchResultTitleCategoryConcept, option.getName());
    }
*/

    public static CategoryConceptListFragment create() {
        CategoryConceptListFragment fragment = new CategoryConceptListFragment();
        return fragment;
    }

    @Override
    protected CategoryConceptListView createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new CategoryConceptListView(getActivity(), 0, 0, false);
    }
}
