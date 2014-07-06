package se.devscout.android.view;

import android.content.Context;
import android.util.AttributeSet;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.SubtitleActivityFilterVisitor;
import se.devscout.android.controller.fragment.TitleActivityFilterVisitor;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.ActivityBankListener;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.SearchHistory;
import se.devscout.server.api.model.UserKey;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryListView extends QuickSearchListView<SearchHistoryListItem> implements ActivityBankListener {

    public SearchHistoryListView(Context context, AttributeSet attrs, int defStyle, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context, attrs, defStyle, emptyMessageTextId, emptyHeaderTextId, isListContentHeight, (List<SearchHistoryListItem>) null);
//        ActivityBankFactory.getInstance(context).addListener(this);
    }

    public SearchHistoryListView(Context context, AttributeSet attrs, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context, attrs, emptyMessageTextId, emptyHeaderTextId, isListContentHeight, (List<SearchHistoryListItem>) null);
//        ActivityBankFactory.getInstance(context).addListener(this);
    }

    public SearchHistoryListView(Context context, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context, emptyMessageTextId, emptyHeaderTextId, isListContentHeight, (List<SearchHistoryListItem>) null);
//        ActivityBankFactory.getInstance(context).addListener(this);
    }

    @Override
    public SearchTask createSearchTask() {
        return new SearchHistorySearchTask();
    }

    @Override
    protected int getImageResId(SearchHistoryListItem item) {
        return R.drawable.ic_action_search;
    }

    @Override
    protected String getTitle(SearchHistoryListItem option) {
        return option.getFilter().toString(new TitleActivityFilterVisitor(getContext()));
    }

    @Override
    protected String getSubtitle(SearchHistoryListItem option) {
        return option.getFilter().toString(new SubtitleActivityFilterVisitor(getContext()));
    }

    @Override
    protected ActivityFilter createFilter(SearchHistoryListItem option) {
        return option.getFilter();
    }

    @Override
    //TODO: Necessary? Remove?
    protected String getSearchResultTitle(SearchHistoryListItem option) {
        return option.getFilter().toString(new TitleActivityFilterVisitor(getContext()));
    }

    @Override
    //TODO: Necessary? Remove?
    public void onSearchHistoryItemAdded(SearchHistory searchHistory) {
//        invalidateResult();
    }

    @Override
    public void onFavouriteChange(ActivityKey activityKey, UserKey userKey, boolean isFavouriteNow) {
    }

    private class SearchHistorySearchTask extends SearchTask {
        @Override
        protected List<SearchHistoryListItem> doSearch() {
            ActivityBank activityBank = ActivityBankFactory.getInstance(getContext());
            List<? extends SearchHistory> searchHistoryList = activityBank.readSearchHistory(0);
            List<SearchHistoryListItem> result = new ArrayList<SearchHistoryListItem>();
            for (SearchHistory searchHistory : searchHistoryList) {
                result.add(new SearchHistoryListItem(searchHistory));
            }
            return result;
        }
    }
}
