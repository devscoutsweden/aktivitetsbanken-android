package se.devscout.android.view;

import android.content.Context;
import se.devscout.android.AgeGroup;
import se.devscout.android.R;
import se.devscout.android.model.activityfilter.ActivityFilter;
import se.devscout.android.util.ActivityBankFactory;

import java.net.URI;
import java.util.Arrays;

public class AgeGroupListView extends QuickSearchListView<AgeGroup> {

    public AgeGroupListView(Context context, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context, emptyMessageTextId, emptyHeaderTextId, isListContentHeight, Arrays.asList(AgeGroup.values()));
    }

    @Override
    protected URI getImageURI(AgeGroup item) {
        return null;
    }

    @Override
    protected int getImageResId(AgeGroup item) {
        return item.getLogoResId();
    }

    @Override
    protected String getTitle(AgeGroup option) {
        return getContext().getString(option.getTitleResId());
    }

    @Override
    protected String getSubtitle(AgeGroup option) {
        return getContext().getString(R.string.ageGroupListItemSubtitleFormat, option.getMin(), option.getMax());
    }

    @Override
    protected ActivityFilter createFilter(AgeGroup option) {
        return ActivityBankFactory.getInstance(getContext()).getFilterFactory().createAgeRangeFilter(option.getScoutAgeRange());
    }

    @Override
    protected String getSearchResultTitle(AgeGroup option) {
        return getContext().getString(R.string.searchResultTitleAgeGroup, getContext().getString(option.getTitleResId()).toLowerCase());
    }

}
