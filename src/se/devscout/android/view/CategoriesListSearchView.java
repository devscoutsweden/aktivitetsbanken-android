package se.devscout.android.view;

import android.content.Context;
import android.util.AttributeSet;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.CategoryListItem;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.SimpleCategoryFilter;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class CategoriesListSearchView extends QuickSearchListView<CategoryListItem> {
    private static final Pattern NOT_A_Z = Pattern.compile("[^a-z_]");

    public CategoriesListSearchView(Context context, AttributeSet attrs, int defStyle, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context, attrs, defStyle, emptyMessageTextId, emptyHeaderTextId, isListContentHeight, Collections.<CategoryListItem>emptyList());
    }

    public CategoriesListSearchView(Context context, AttributeSet attrs, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context, attrs, emptyMessageTextId, emptyHeaderTextId, isListContentHeight, Collections.<CategoryListItem>emptyList());
    }

    public CategoriesListSearchView(Context context, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context, emptyMessageTextId, emptyHeaderTextId, isListContentHeight, Collections.<CategoryListItem>emptyList());
    }

    @Override
    public SearchTask createSearchTask() {
        return new MySearchTask();
    }

    @Override
    protected int getImageResId(CategoryListItem item) {
        int resourceId = getResourceId(item.getGroup() + "_" + item.getName(), "drawable");
        return resourceId > 0 ? resourceId : getResourceId(item.getGroup(), "drawable");
    }

    private int getResourceId(String text, String type) {
        String resName = toResourceName(text);
        // TODO Fix hard-coded icons.
        if ("scoutmethod_friluftsliv".equals(resName)) {
            return R.drawable.scout_method_outdoors;
        } else if ("scoutmethod_patrullen".equals(resName)) {
            return R.drawable.scout_method_troupe;
        } else {
            String packageName = getContext().getPackageName();
            int identifier = getResources().getIdentifier(resName, type, packageName);
            return identifier;
        }
    }

    private String toResourceName(String name) {
        return NOT_A_Z.matcher(name.toLowerCase()).replaceAll("");
    }

    @Override
    protected String getTitle(CategoryListItem option) {
        int resourceId = getResourceId(option.getName(), "string");
        return resourceId > 0 ? getContext().getString(resourceId) : option.getName();
    }

    @Override
    protected String getSubtitle(CategoryListItem option) {
        int resourceId = getResourceId(option.getGroup(), "string");
        return resourceId > 0 ? getContext().getString(resourceId) : option.getGroup();
    }

    @Override
    protected ActivityFilter createFilter(CategoryListItem option) {
        return new SimpleCategoryFilter(option);
    }

    @Override
    protected String getSearchResultTitle(CategoryListItem option) {
        return getContext().getString(R.string.searchResultTitleCategoryTrack, option.getName());
    }

    private class MySearchTask extends SearchTask {
        @Override
        protected List<CategoryListItem> doSearch() {
            List<? extends Category> categories = ActivityBankFactory.getInstance(getContext()).readCategories();
            List<CategoryListItem> result = new ArrayList<CategoryListItem>();
            for (Category category : categories) {
                result.add(new CategoryListItem(category));
            }
            return result;
        }
    }
}
