package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.view.CategoriesListSearchView;

import java.util.Collections;
import java.util.regex.Pattern;

public class CategoriesListFragment extends QuickSearchListFragment<CategoryListItem, CategoriesListSearchView> {

    private static final Pattern NOT_A_Z = Pattern.compile("[^a-z_]");

    public CategoriesListFragment() {
        // Send empty list to superclass. This does not matter since doSearch() is overridden.
        super(Collections.<CategoryListItem>emptyList());
    }

    @Override
    protected CategoriesListSearchView createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new CategoriesListSearchView(getActivity(), R.string.searchResultEmptyMessage, R.string.searchResultEmptyTitle, false);
    }

    public static CategoriesListFragment create() {
        CategoriesListFragment fragment = new CategoriesListFragment();
        return fragment;
    }
}
