package se.devscout.android.view;

import android.content.Context;
import se.devscout.android.CategoryTrack;
import se.devscout.android.R;
import se.devscout.android.util.SimpleCategoryFilter;
import se.devscout.server.api.ActivityFilter;

import java.net.URI;
import java.util.Arrays;

public class CategoryTrackListView extends QuickSearchListView<CategoryTrack> {

    public CategoryTrackListView(Context context, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context, emptyMessageTextId, emptyHeaderTextId, isListContentHeight, Arrays.asList(CategoryTrack.values()));
    }

    @Override
    protected URI getImageURI(CategoryTrack item) {
        return null;
    }

    @Override
    protected int getImageResId(CategoryTrack item) {
        return item.getLogoResId();
    }

    @Override
    protected String getTitle(CategoryTrack option) {
        return getContext().getString(option.getTitleResId());
    }

    @Override
    protected String getSubtitle(CategoryTrack option) {
        return null;
    }

    @Override
    protected ActivityFilter createFilter(CategoryTrack option) {
        return new SimpleCategoryFilter(
                option.getScoutCategoryTrack().getGroup(),
                option.getScoutCategoryTrack().getName(),
                option.getScoutCategoryTrack().getServerId());
    }

    @Override
    protected String getSearchResultTitle(CategoryTrack option) {
        return getContext().getString(R.string.searchResultTitleCategoryTrack, option.getName());
    }
}
