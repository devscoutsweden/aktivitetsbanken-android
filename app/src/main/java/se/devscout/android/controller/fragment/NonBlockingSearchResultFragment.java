package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.util.LogUtil;
import se.devscout.android.view.NonBlockingSearchView;

import java.io.Serializable;

public abstract class NonBlockingSearchResultFragment<T extends Serializable, V extends NonBlockingSearchView<T>> extends ActivityBankFragment /*implements AdapterView.OnItemClickListener */{
    private boolean mIsResultLoaded;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            /*
             * Restore fields from saved state, for example after the device has been rotated.
             */
            mIsResultLoaded = savedInstanceState.getBoolean("mIsResultLoaded");
        }

        final V view = createView(inflater, container, savedInstanceState);
        view.setId(111);

        return view;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        if (!getListView().isResultPresent()) {
            getListView().runSearchTaskInNewThread();
        }
    }

    protected V getListView() {
        return (V) getView().findViewById(111);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected abstract V createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
         * Store fields into saved state, for example when the activity is destroyed after the device has been rotated.
         */
        LogUtil.d(ActivitiesListFragment.class.getName(), "Saving state");
        outState.putBoolean("mIsResultLoaded", mIsResultLoaded);
        LogUtil.d(ActivitiesListFragment.class.getName(), "State saved");
    }
}
