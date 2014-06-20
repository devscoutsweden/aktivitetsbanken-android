package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.view.SearchHistoryListView;
import se.devscout.server.api.ActivityBankListener;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.SearchHistory;
import se.devscout.server.api.model.UserKey;

import java.util.List;

public class SearchHistoryListFragment extends QuickSearchListFragment<SearchHistoryListItem, SearchHistoryListView> implements ActivityBankListener {

    private boolean mRefreshResultOnResume = false;

    public SearchHistoryListFragment() {
        // Send empty list to superclass. This does not matter since doSearch() is overridden.
        super((List<SearchHistoryListItem>)null);
    }

/*
    @Override
    protected List<E> doSearch() {
        ActivityBank activityBank = ActivityBankFactory.getInstance(getActivity());
        mItems = ((List<E>) activityBank.readSearchHistory(0));
        return mItems;
    }

    @Override
    protected E getResultObjectFromId(ObjectIdentifierPojo identifier) {
        if (mItems == null) {
            doSearch();
        }
        return super.getResultObjectFromId(identifier);    //To change body of overridden methods use File | Settings | File Templates.
    }
*/

    @Override
    protected SearchHistoryListView createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new SearchHistoryListView(getActivity(), 0, 0, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivityBank().addListener(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

/*
    @Override
    protected void refreshResultList(boolean force) {
        super.refreshResultList(true);
    }

    @Override
    protected int getImageResId(E item) {
        return R.drawable.ic_action_search;
    }

    @Override
    protected String getTitle(E option) {
        return option.getData().getFilter().toString(new TitleActivityFilterVisitor(getActivity()));
    }

    @Override
    protected String getSubtitle(E option) {
        return option.getData().getFilter().toString(new SubtitleActivityFilterVisitor(getActivity()));
    }

    @Override
    protected ActivityFilter createFilter(E option) {
        return option.getData().getFilter();
    }

    @Override
    protected String getSearchResultTitle(E option) {
        return option.getData().getFilter().toString(new TitleActivityFilterVisitor(getActivity()));
    }
*/

    public static SearchHistoryListFragment create() {
        return new SearchHistoryListFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        synchronized (this) {
            if (mRefreshResultOnResume) {
                getListView().createSearchTask().execute();
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
}
