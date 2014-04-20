package se.devscout.android.controller.fragment;

import se.devscout.android.AgeGroup;
import se.devscout.android.R;
import se.devscout.android.util.AgeRangeFilter;
import se.devscout.server.api.ActivityFilter;

public class AgeGroupListFragment extends QuickSearchListFragment<AgeGroup> {

    public AgeGroupListFragment() {
        super(AgeGroup.values());
    }

    @Override
    protected int getImageResId(AgeGroup item) {
        return item.getLogoResId();
    }

    @Override
    protected String getTitle(AgeGroup option) {
        return getString(option.getTitleResId());
    }

    @Override
    protected String getSubtitle(AgeGroup option) {
        return getString(R.string.ageGroupListItemSubtitleFormat, option.getMin(), option.getMax());
    }

    @Override
    protected ActivityFilter createFilter(AgeGroup option) {
        return new AgeRangeFilter(option.getScoutAgeRange());
    }

    @Override
    protected String getSearchResultTitle(AgeGroup option) {
        return getString(R.string.searchResultTitleAgeGroup, option.getName());
    }

    public static AgeGroupListFragment create() {
        AgeGroupListFragment fragment = new AgeGroupListFragment();
        return fragment;
    }
}
