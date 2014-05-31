package se.devscout.android.controller.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import se.devscout.android.R;
import se.devscout.android.model.ObjectIdentifierPojo;
import se.devscout.server.api.model.ObjectIdentifier;

import java.util.ArrayList;
import java.util.List;

public abstract class NonBlockingSearchResultFragment<T extends ObjectIdentifier> extends ActivityBankFragment implements AdapterView.OnItemClickListener {
    /**
     * Keeps track of which items were shown when the activity (fragment) was "unloaded".
     */
    private ArrayList<ObjectIdentifierPojo> mObjectIdentifiers;
    private ListView mList;
    private FrameLayout mProgressView;
    private View mEmptyView;
    private int mEmptyHeaderTextId;
    private int mEmptyMessageTextId;

    protected NonBlockingSearchResultFragment() {
        this(0, R.string.searchResultDefaultEmptyMessage);
    }

    protected NonBlockingSearchResultFragment(int emptyHeaderTextId, int emptyMessageTextId) {
        mEmptyHeaderTextId = emptyHeaderTextId;
        mEmptyMessageTextId = emptyMessageTextId;
    }

    protected abstract List<T> doSearch();

    protected abstract ArrayAdapter<T> createAdapter(List<T> result);

    protected abstract T getResultObjectFromId(ObjectIdentifierPojo identifier);

    public ArrayAdapter<T> getListAdapter() {
        return (ArrayAdapter<T>) mList.getAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            /*
             * Restore fields from saved state, for example after the device has been rotated.
             */
            mObjectIdentifiers = (ArrayList<ObjectIdentifierPojo>) savedInstanceState.getSerializable("mObjectIdentifiers");
            Log.d(ActivitiesListFragment.class.getName(), "State (e.g. search result) has been restored.");
        }

        final View view = inflater.inflate(R.layout.search_result, container, false);

        mProgressView = (FrameLayout) view.findViewById(R.id.searchResultProgress);
        mList = (ListView) view.findViewById(R.id.searchResultList);
        mEmptyView = view.findViewById(R.id.searchResultEmpty);
        initEmptyViewText(view, R.id.searchResultEmptyHeader, mEmptyHeaderTextId);
        initEmptyViewText(view, R.id.searchResultEmptyMessage, mEmptyMessageTextId);
        mList.setEmptyView(mEmptyView);

        refreshResultList(false);

        return view;
    }

    private void initEmptyViewText(View view, int textViewId, int textId) {
        TextView emptyMessageTextView = (TextView) view.findViewById(textViewId);
        if (textId > 0) {
            emptyMessageTextView.setText(textId);
        } else {
            emptyMessageTextView.setVisibility(View.GONE);
        }
    }

    protected void invalidateResult() {
        mObjectIdentifiers = null;
    }

    protected void refreshResultList(boolean force) {
        if (force) {
            invalidateResult();
        }
        mList.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mList.setOnItemClickListener(this);

        if (mObjectIdentifiers != null) {
            // Result exits
            Log.d(ActivitiesListFragment.class.getName(), "Result exists. Display it.");
            showResult(getSearchResult());
        } else {
            // Start search in separate thread
            Log.d(ActivitiesListFragment.class.getName(), "Result has not been returned/cached. Starting search task in new thread.");

            AsyncTask<Void, Void, List<T>> task = new AsyncTask<Void, Void, List<T>>() {
                @Override
                protected List<T> doInBackground(Void... voids) {
                    Log.d(ActivitiesListFragment.class.getName(), "Start of doInBackground as part of " + NonBlockingSearchResultFragment.this.getClass().getName());
                    List<T> list = NonBlockingSearchResultFragment.this.doSearch();
                    Log.d(ActivitiesListFragment.class.getName(), "End of doInBackground as part of " + NonBlockingSearchResultFragment.this.getClass().getName());
                    return list;
                }

                @Override
                protected void onPostExecute(List<T> result) {
                    Log.d(ActivitiesListFragment.class.getName(), "Search task has completed. " + result.size() + " were returned.");
                    showResult(result);
                }
            };
            task.execute();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
         * Store fields into saved state, for example when the activity is destroyed after the device has been rotated.
         */
        Log.d(ActivitiesListFragment.class.getName(), "Saving state");
        outState.putSerializable("mObjectIdentifiers", mObjectIdentifiers);
        Log.d(ActivitiesListFragment.class.getName(), "State saved");
    }

    protected List<T> getSearchResult() {
        ArrayList<T> list = new ArrayList<T>();
        for (ObjectIdentifierPojo id : mObjectIdentifiers) {
            //TODO: Save complete Activity objects in mObjectIdentifiers instead of only the keys? Performance gain or performance loss?
            list.add(getResultObjectFromId(id));
        }
        return list;
    }

    protected void onSearchDone(List<T> result) {
    }

    protected void showResult(List<T> result) {
        if (getActivity() != null) {
            mList.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.VISIBLE);
            mList.setAdapter(createAdapter(result));

            mProgressView.setVisibility(View.GONE);
            Log.d(ActivitiesListFragment.class.getName(), "Progress view has been hidden and list view has been shown.");
        } else {
            Log.d(ActivitiesListFragment.class.getName(), "Result not available. Result cannot be shown.");
        }
        initObjectIdentifiers(result);
        onSearchDone(result);
    }

    private void initObjectIdentifiers(List<T> result) {
        ArrayList<ObjectIdentifierPojo> list = new ArrayList<ObjectIdentifierPojo>();
        for (T key : result) {
            list.add(new ObjectIdentifierPojo(key.getId()));
        }
        mObjectIdentifiers = list;
    }

}
