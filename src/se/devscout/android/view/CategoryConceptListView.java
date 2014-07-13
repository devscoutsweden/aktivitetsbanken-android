package se.devscout.android.view;

import android.content.Context;
import se.devscout.android.CategoryConcept;
import se.devscout.android.R;
import se.devscout.android.util.SimpleCategoryFilter;
import se.devscout.server.api.ActivityFilter;

import java.util.Arrays;

public class CategoryConceptListView extends QuickSearchListView<CategoryConcept> {

    public CategoryConceptListView(Context context, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context, emptyMessageTextId, emptyHeaderTextId, isListContentHeight, Arrays.asList(CategoryConcept.values()));
    }

    @Override
    protected int getImageResId(CategoryConcept item) {
        return item.getLogoResId();
    }

    @Override
    protected String getTitle(CategoryConcept option) {
        return getContext().getString(option.getTitleResId());
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
        return getContext().getString(R.string.searchResultTitleCategoryConcept, option.getName());
    }
}
