package se.devscout.android.view;

import android.content.Context;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.SubtitleActivityFilterVisitor;
import se.devscout.android.controller.fragment.TitleActivityFilterVisitor;
import se.devscout.android.model.SearchHistory;
import se.devscout.android.model.activityfilter.ActivityFilter;
import se.devscout.android.model.repo.ActivityBank;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.auth.CredentialsManager;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class SearchHistoryListView extends QuickSearchListView<SearchHistoryListItem> {

    public SearchHistoryListView(Context context, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context, emptyMessageTextId, emptyHeaderTextId, isListContentHeight, (List<SearchHistoryListItem>) null);
    }

    @Override
    protected URI getImageURI(SearchHistoryListItem item) {
        return null;
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
        return option.getFilter().visit(new TitleActivityFilterVisitor(getContext()));
    }

    @Override
    protected String getSubtitle(SearchHistoryListItem option) {
        return option.getFilter().visit(new SubtitleActivityFilterVisitor(getContext()));
    }

    @Override
    protected ActivityFilter createFilter(SearchHistoryListItem option) {
        return option.getFilter();
    }

    @Override
    protected String getSearchResultTitle(SearchHistoryListItem option) {
        return option.getFilter().visit(new TitleActivityFilterVisitor(getContext()));
    }

    private class SearchHistorySearchTask extends SearchTask {
        @Override
        protected List<SearchHistoryListItem> doSearch() {
            ActivityBank activityBank = ActivityBankFactory.getInstance(getContext());
            List<? extends SearchHistory> searchHistoryList = activityBank.readSearchHistory(0, CredentialsManager.getInstance(getContext()).getCurrentUser());
            List<SearchHistoryListItem> result = new ArrayList<SearchHistoryListItem>();
            for (SearchHistory searchHistory : searchHistoryList) {
                result.add(new SearchHistoryListItem(searchHistory));
            }
            return result;
        }
    }
}
