package se.devscout.android.view;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.*;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivitiesListFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class NonBlockingSearchView<T extends Serializable> extends FrameLayout implements AdapterView.OnItemClickListener, View.OnClickListener {
    /**
     * Keeps track of which items were shown when the activity (fragment) was "unloaded".
     */
    protected boolean mRefreshResultOnResume = false;

    private ArrayList<T> mResult;
    private ViewGroup mList;
    private FrameLayout mProgressView;
    private View mEmptyView;
    private int mEmptyHeaderTextId;
    private int mEmptyMessageTextId;

    public NonBlockingSearchView(Context context, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context);
        init(context, emptyMessageTextId, emptyHeaderTextId, isListContentHeight);
    }

    public NonBlockingSearchView(Context context, AttributeSet attrs, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context, attrs);
        init(context, emptyMessageTextId, emptyHeaderTextId, isListContentHeight);
    }

    public NonBlockingSearchView(Context context, AttributeSet attrs, int defStyle, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context, attrs, defStyle);
        init(context, emptyMessageTextId, emptyHeaderTextId, isListContentHeight);
    }

    private void init(Context context, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        mEmptyMessageTextId = emptyMessageTextId;
        mEmptyHeaderTextId = emptyHeaderTextId;

        if (isListContentHeight) {
            initFullLengthView(context);
        } else {
            initScrollingList(context);
        }
        mProgressView = (FrameLayout) findViewById(R.id.searchResultProgress);
        mEmptyView = findViewById(R.id.searchResultEmpty);

        initEmptyViewText(R.id.searchResultEmptyHeader, mEmptyHeaderTextId);
        initEmptyViewText(R.id.searchResultEmptyMessage, mEmptyMessageTextId);

//        mList.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, isListContentHeight ? getResources().getDimensionPixelSize(android.R.dimen.thumbnail_height) * 2 /*ViewGroup.LayoutParams.WRAP_CONTENT*/ : LayoutParams.MATCH_PARENT));
//        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, isListContentHeight ? ViewGroup.LayoutParams.WRAP_CONTENT : LayoutParams.MATCH_PARENT));
    }

    private void initScrollingList(Context context) {
        LayoutInflater.from(context).inflate(R.layout.search_result, this, true);

        ListView listView = (ListView) findViewById(R.id.searchResultList);
        listView.setEmptyView(mEmptyView);
        listView.setOnItemClickListener(this);
        mList = listView;
    }

    private void initFullLengthView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.search_result_widget, this, true);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.searchResultList);
//        linearLayout.setOnClickListener(this);
        mList = linearLayout;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();    //To change body of overridden methods use File | Settings | File Templates.
    }

//    protected abstract List<T> doSearch();

    protected abstract ArrayAdapter<T> createAdapter(List<T> result);

//    protected abstract T getResultObjectFromId(ObjectIdentifierPojo identifier);

/*
    public ArrayAdapter<T> getListAdapter() {
        return (ArrayAdapter<T>) mList.getAdapter();
    }
*/

    protected void sort(Comparator<T> sorter) {
        if (mList instanceof ListView) {
            ListView listView = (ListView) mList;
            ((ArrayAdapter<T>)listView.getAdapter()).sort(sorter);
        } else {
            Log.e(NonBlockingSearchView.class.getName(), "Cannot only sort ListView");
        }
    }

    @Override
    public void onClick(View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parent;
            int position = viewGroup.indexOfChild(view);
            onItemClick(view, position);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        onItemClick(view, position);
    }

    protected abstract void onItemClick(View view, int position);

    protected List<T> getItems() {
        return mResult;
    }

    private void initEmptyViewText(int textViewId, int textId) {
        TextView emptyMessageTextView = (TextView) findViewById(textViewId);
        if (textId > 0) {
            emptyMessageTextView.setText(textId);
        }
        emptyMessageTextView.setVisibility(View.GONE);
    }

/*
    protected void invalidateResult() {
        mObjectIdentifiers = null;
    }
*/

/*
    public void refreshResultList(boolean force) {
        if (force) {
            invalidateResult();
        }
        mList.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mList.setOnItemClickListener(this);

        if (mObjectIdentifiers != null) {
            // Result exits
            Log.d(ActivitiesListFragment.class.getName(), "Result exists. Display it.");
            setResult(getSearchResult());
        } else {
            // Start search in separate thread
            Log.d(ActivitiesListFragment.class.getName(), "Result has not been returned/cached. Starting search task in new thread.");

            AsyncTask<Void, Void, List<T>> task = new AsyncTask<Void, Void, List<T>>() {
                @Override
                protected List<T> doInBackground(Void... voids) {
                    Log.d(ActivitiesListFragment.class.getName(), "Start of doInBackground as part of " + NonBlockingSearchView.this.getClass().getName());
                    List<T> list = NonBlockingSearchView.this.doSearch();
                    Log.d(ActivitiesListFragment.class.getName(), "End of doInBackground as part of " + NonBlockingSearchView.this.getClass().getName());
                    return list;
                }

                @Override
                protected void onPostExecute(List<T> result) {
                    Log.d(ActivitiesListFragment.class.getName(), "Search task has completed. " + result.size() + " were returned.");
                    setResult(result);
                }
            };
            task.execute();
        }
    }
*/

/*
    protected List<T> getSearchResult() {
        ArrayList<T> list = new ArrayList<T>();
        for (ObjectIdentifierPojo id : mObjectIdentifiers) {
            //TODO: Save complete Activity objects in mObjectIdentifiers instead of only the keys? Performance gain or performance loss?
            list.add(getResultObjectFromId(id));
        }
        return list;
    }
*/

    protected void onSearchDone(List<T> result) {
    }

    public void setResult(List<T> result) {
//        if (getActivity() != null) {
        ArrayAdapter<T> adapter = createAdapter(result);



        if (mList instanceof ListView) {
            mList.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.VISIBLE);

            ListView listView = (ListView) mList;
            listView.setAdapter(adapter);
        } else {
            mList.setVisibility(adapter.isEmpty() ? View.GONE : View.VISIBLE);
            mEmptyView.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);

            LinearLayout linearLayout = (LinearLayout) mList;
            linearLayout.removeAllViews();
            for (int i = 0; i < adapter.getCount(); i++) {
                View view = adapter.getView(i, null, linearLayout);
                view.setOnClickListener(this);
                linearLayout.addView(view);
            }
        }




/*
        boolean isListContentHeight = mList.getLayoutParams().height != LayoutParams.MATCH_PARENT;
        if (isListContentHeight) {
            int height = 0;
            for (int i = 0; i < mList.getChildCount(); i++) {
                height = mList.getChildAt(i).getMeasuredHeight();
            }
            height += (mList.getChildCount() - 1) * mList.getDividerHeight();
            mList.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height */
/*getResources().getDimensionPixelSize(android.R.dimen.thumbnail_height) * result.size()*//*
));
        }
*/
        mResult = new ArrayList<T>(result);

        mProgressView.setVisibility(View.GONE);
        Log.d(ActivitiesListFragment.class.getName(), "Progress view has been hidden and list view has been shown.");

        onSearchDone(result);
    }

/*
    private void initObjectIdentifiers(List<T> result) {
        ArrayList<ObjectIdentifierPojo> list = new ArrayList<ObjectIdentifierPojo>();
        for (T key : result) {
            list.add(new ObjectIdentifierPojo(key.getId()));
        }
        setObjectIdentifiers(list);
    }
*/

/*
    public void setObjectIdentifiers(ArrayList<ObjectIdentifierPojo> objectIdentifiers) {
        mObjectIdentifiers = objectIdentifiers;
    }
*/

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putSerializable("mResult", mResult);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mResult = (ArrayList<T>) bundle.getSerializable("mResult");
            state = bundle.getParcelable("instanceState");

            setResult(mResult);
        }
        super.onRestoreInstanceState(state);

//        refreshResultList(false);
    }

    public abstract SearchTask createSearchTask();

    public boolean isResultPresent() {
        return !mRefreshResultOnResume && mResult != null;
    }

    public abstract class SearchTask extends AsyncTask<Object, Object, List<T>> {
        @Override
        protected List<T> doInBackground(Object... voids) {
            Log.d(NonBlockingSearchView.class.getName(), "Start of doInBackground as part of " + NonBlockingSearchView.this.getClass().getName());
            List<T> list = doSearch();
            Log.d(NonBlockingSearchView.class.getName(), "End of doInBackground as part of " + NonBlockingSearchView.this.getClass().getName());
            return list;
        }

        @Override
        protected void onPostExecute(List<T> result) {
            Log.d(NonBlockingSearchView.class.getName(), "Search task has completed. " + result.size() + " were returned.");
            setResult(result);
        }

        protected abstract List<T> doSearch();

    }
}