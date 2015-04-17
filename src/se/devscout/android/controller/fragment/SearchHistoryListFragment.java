package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.view.SearchHistoryListItem;
import se.devscout.android.view.SearchHistoryListView;

import java.util.List;
public class SearchHistoryListFragment extends QuickSearchListFragment<SearchHistoryListItem, SearchHistoryListView> {

    private long mSearchHistoryModificationCounter;

    public SearchHistoryListFragment() {
        // Send empty list to superclass. This does not matter since doSearch() is overridden.
        super((List<SearchHistoryListItem>)null);
    }

    @Override
    protected SearchHistoryListView createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new SearchHistoryListView(getActivity(), R.string.searchHistoryEmptyMessage, R.string.searchHistoryEmptyTitle, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        synchronized (this) {
            if (savedInstanceState != null) {
                mSearchHistoryModificationCounter = savedInstanceState.getLong("mSearchHistoryModificationCounter");
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static SearchHistoryListFragment create() {
        return new SearchHistoryListFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        synchronized (this) {
            long latest = getActivityBank().getModificationCounters().getSearchHistory();
            if (mSearchHistoryModificationCounter < latest || !getListView().isResultPresent()) {
                mSearchHistoryModificationCounter = latest;
                getListView().runSearchTaskInNewThread();
            }
        }
    }
}
