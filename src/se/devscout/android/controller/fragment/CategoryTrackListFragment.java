package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import se.devscout.android.CategoryTrack;
import se.devscout.android.view.CategoryTrackListView;

public class CategoryTrackListFragment extends QuickSearchListFragment<CategoryTrack, CategoryTrackListView> {

    public CategoryTrackListFragment() {
        super(CategoryTrack.values());
    }

/*
    @Override
    protected int getImageResId(CategoryTrack item) {
        return item.getLogoResId();
    }

    @Override
    protected String getTitle(CategoryTrack option) {
        return getString(option.getTitleResId());
    }

    @Override
    protected String getSubtitle(CategoryTrack option) {
        return null;
    }

    @Override
    protected ActivityFilter createFilter(CategoryTrack option) {
        return new SimpleCategoryFilter(option.getScoutCategoryTrack());
    }

    @Override
    protected String getSearchResultTitle(CategoryTrack option) {
        return getString(R.string.searchResultTitleCategoryTrack, option.getName());
    }
*/

    public static CategoryTrackListFragment create() {
        CategoryTrackListFragment fragment = new CategoryTrackListFragment();
        return fragment;
    }

    @Override
    protected CategoryTrackListView createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new CategoryTrackListView(getActivity(), 0, 0, false);
    }
}
