package se.devscout.android.controller.fragment;

import se.devscout.android.R;
import se.devscout.android.model.ObjectIdentifierPojo;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.SearchHistory;

import java.util.List;

public class SearchHistoryListFragment<E extends SearchHistory> extends QuickSearchListFragment<E> {

    public SearchHistoryListFragment() {
        // Send empty list to superclass. This does not matter since doSearch() is overridden.
        super((List<E>)null);
    }

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

    public static SearchHistoryListFragment create() {
        return new SearchHistoryListFragment();
    }

}
