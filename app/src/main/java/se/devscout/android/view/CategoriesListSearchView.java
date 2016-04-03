package se.devscout.android.view;

import android.content.Context;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.devscout.android.R;
import se.devscout.android.controller.fragment.CategoryListItem;
import se.devscout.android.model.Category;
import se.devscout.android.model.Media;
import se.devscout.android.model.activityfilter.ActivityFilter;
import se.devscout.android.model.repo.ActivityBank;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.SimpleCategoryFilter;
import se.devscout.android.util.http.UnauthorizedException;

public class CategoriesListSearchView extends QuickSearchListView<CategoryListItem> {

    public CategoriesListSearchView(Context context, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context, emptyMessageTextId, emptyHeaderTextId, isListContentHeight, Collections.<CategoryListItem>emptyList());
    }

    @Override
    public SearchTask createSearchTask() {
        return new GetCategoryListTask();
    }

    @Override
    protected URI getImageURI(CategoryListItem item) {
        Media media = ActivityBankFactory.getInstance(getContext()).readMediaItem(item.getIconMediaKey());
        final int width = getContext().getResources().getDimensionPixelSize(R.dimen.uiBlockSize);
        URI imageURI = media != null ? ActivityBankFactory.getInstance(getContext()).getMediaItemURI(media, width, width) : null;
        if (media != null) {
            return imageURI;
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
        if ("aktivitetsbanken-track".equals(option.getGroup())) {
            return getResources().getString(R.string.searchResultTitleCategoryTrack, option.getName());
        } else if ("aktivitetsbanken-concept".equals(option.getGroup())) {
            return getResources().getString(R.string.searchResultTitleCategoryConcept, option.getName());
        } else {
            return option.getName();
        }
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

    private class GetCategoryListTask extends SearchTask {

        @Override
        protected List<CategoryListItem> doSearch() throws UnauthorizedException {
            List<? extends Category> categories = ActivityBankFactory.getInstance(getContext()).readCategories(ActivityBank.DEFAULT_MINIMUM_ACTIVITIES_PER_CATEGORY);
            List<CategoryListItem> result = new ArrayList<CategoryListItem>();
            for (Category category : categories) {
                if (category.getActivitiesCount() == null || category.getActivitiesCount() != 0) {
                    result.add(new CategoryListItem(category));
                }
            }
            Collections.sort(result, new Comparator<CategoryListItem>() {
                @Override
                public int compare(CategoryListItem categoryListItem, CategoryListItem t1) {
                    return categoryListItem.getName().compareTo(t1.getName());
                }
            });
            return result;
        }
    }
}
