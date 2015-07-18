package se.devscout.android.view;

import android.content.Context;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.CategoryListItem;
import se.devscout.android.model.Category;
import se.devscout.android.model.Media;
import se.devscout.android.model.activityfilter.ActivityFilter;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.SimpleCategoryFilter;
import se.devscout.android.util.http.UnauthorizedException;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoriesListSearchView extends QuickSearchListView<CategoryListItem> {

    public CategoriesListSearchView(Context context, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context, emptyMessageTextId, emptyHeaderTextId, isListContentHeight, Collections.<CategoryListItem>emptyList());
    }

    @Override
    public SearchTask createSearchTask() {
        return new MySearchTask();
    }

    @Override
    protected URI getImageURI(CategoryListItem item) {
        Media media = ActivityBankFactory.getInstance(getContext()).readMediaItem(item.getIconMediaKey());
        if (media != null) {
            return media.getURI();
        } else {
            return null;
        }
    }

    @Override
    protected int getImageResId(CategoryListItem item) {
        return R.drawable.ic_action_labels;
    }

    @Override
    protected String getTitle(CategoryListItem option) {
        return option.getName();
    }

    @Override
    protected String getSubtitle(CategoryListItem option) {
        if (option.getActivitiesCount() != null) {
            return getResources().getQuantityString(R.plurals.searchResultSubtitleCategoryTrack, option.getActivitiesCount(), option.getActivitiesCount());
        } else {
            // This can happen if the network connection times out and the SQLite database contains no information in the activities_count column.
            return null;
        }
    }

    @Override
    protected ActivityFilter createFilter(CategoryListItem option) {
        return new SimpleCategoryFilter(option.getGroup(), option.getName(), option.getServerId());
    }

    @Override
    protected String getSearchResultTitle(CategoryListItem option) {
        return getContext().getString(R.string.searchResultTitleCategoryTrack, option.getName());
    }

    private class MySearchTask extends SearchTask {
        @Override
        protected List<CategoryListItem> doSearch() throws UnauthorizedException {
            List<? extends Category> categories = ActivityBankFactory.getInstance(getContext()).readCategories();
            List<CategoryListItem> result = new ArrayList<CategoryListItem>();
            for (Category category : categories) {
                if (category.getActivitiesCount() == null || category.getActivitiesCount() != 0) {
                    result.add(new CategoryListItem(category));
                }
            }
            return result;
        }
    }
}
