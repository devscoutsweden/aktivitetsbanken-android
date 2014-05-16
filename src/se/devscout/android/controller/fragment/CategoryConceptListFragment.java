package se.devscout.android.controller.fragment;

import se.devscout.android.CategoryConcept;
import se.devscout.android.R;
import se.devscout.android.util.SimpleCategoryFilter;
import se.devscout.server.api.ActivityFilter;

public class CategoryConceptListFragment extends QuickSearchListFragment<CategoryConcept> {

    public CategoryConceptListFragment() {
        super(CategoryConcept.values());
    }

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

    public static CategoryConceptListFragment create() {
        CategoryConceptListFragment fragment = new CategoryConceptListFragment();
        return fragment;
    }
}
