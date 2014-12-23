package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.view.SearchHistoryListItem;
import se.devscout.android.view.SearchHistoryListView;
import se.devscout.server.api.ActivityBankListener;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.SearchHistory;
import se.devscout.server.api.model.UserKey;

import java.util.List;
//TODO: It might be cleaner to create an AbstractActivityBankListener instead of implementing ActivityBankListener
public class SearchHistoryListFragment extends QuickSearchListFragment<SearchHistoryListItem, SearchHistoryListView> implements ActivityBankListener {

    private boolean mRefreshResultOnResume = false;

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
//        getActivityBank().addListener(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static SearchHistoryListFragment create() {
        return new SearchHistoryListFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        synchronized (this) {
            if (mRefreshResultOnResume) {
                //TODO: Necessary? Remove?
                getListView().runSearchTaskInNewThread();
            }
        }
    }

    @Override
    public void onSearchHistoryItemAdded(SearchHistory searchHistory) {
        synchronized (this) {
            mRefreshResultOnResume = true;
        }
    }

    @Override
    public void onFavouriteChange(ActivityKey activityKey, UserKey userKey, boolean isFavouriteNow) {
    }

    @Override
    public void onLogIn() {
    }

    @Override
    public void onLogOut() {
    }

    @Override
    public void onAsyncException(Exception e) {
    }
}
