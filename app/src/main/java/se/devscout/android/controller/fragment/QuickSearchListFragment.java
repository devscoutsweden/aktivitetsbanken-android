package se.devscout.android.controller.fragment;

import se.devscout.android.view.NonBlockingSearchView;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

abstract class QuickSearchListFragment<T extends Serializable, V extends NonBlockingSearchView<T>> extends NonBlockingSearchResultFragment<T, V> {

    protected List<T> mItems;

    protected QuickSearchListFragment(List<T> items) {
        mItems = items;
    }

    protected QuickSearchListFragment(T[] items) {
        this(Arrays.asList(items));
    }

}
