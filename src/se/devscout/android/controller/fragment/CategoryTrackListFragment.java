package se.devscout.android.controller.fragment;

import se.devscout.android.CategoryTrack;
import se.devscout.android.R;
import se.devscout.android.util.SimpleCategoryFilter;
import se.devscout.server.api.ActivityFilter;

public class CategoryTrackListFragment extends QuickSearchListFragment<CategoryTrack> {

    public CategoryTrackListFragment() {
        super(CategoryTrack.values());
    }

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

    public static CategoryTrackListFragment create() {
        CategoryTrackListFragment fragment = new CategoryTrackListFragment();
        return fragment;
    }
}
