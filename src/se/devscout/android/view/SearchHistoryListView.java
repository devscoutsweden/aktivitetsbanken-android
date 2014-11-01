package se.devscout.android.view;

import android.content.Context;
import android.widget.ImageView;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.SubtitleActivityFilterVisitor;
import se.devscout.android.controller.fragment.TitleActivityFilterVisitor;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.PreferencesUtil;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.SearchHistory;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryListView extends QuickSearchListView<SearchHistoryListItem> {

    public SearchHistoryListView(Context context, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context, emptyMessageTextId, emptyHeaderTextId, isListContentHeight, (List<SearchHistoryListItem>) null);
    }

    @Override
    public SearchTask createSearchTask() {
        return new SearchHistorySearchTask();
    }

    @Override
    protected int getImageResId(SearchHistoryListItem item, ImageView imageView) {
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
    protected String getSearchResultTitle(SearchHistoryListItem option) {
        return option.getFilter().toString(new TitleActivityFilterVisitor(getContext()));
    }

    private class SearchHistorySearchTask extends SearchTask {
        @Override
        protected List<SearchHistoryListItem> doSearch() {
            ActivityBank activityBank = ActivityBankFactory.getInstance(getContext());
            List<? extends SearchHistory> searchHistoryList = activityBank.readSearchHistory(0, PreferencesUtil.getInstance(getContext()).getCurrentUser());
            List<SearchHistoryListItem> result = new ArrayList<SearchHistoryListItem>();
            for (SearchHistory searchHistory : searchHistoryList) {
                result.add(new SearchHistoryListItem(searchHistory));
            }
            return result;
        }
    }
}
