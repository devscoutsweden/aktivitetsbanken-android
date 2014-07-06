package se.devscout.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
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

public abstract class NonBlockingSearchView<T extends Serializable> extends FrameLayout implements AdapterView.OnItemClickListener, View.OnClickListener/*, FragmentListener */{
    /**
     * Keeps track of which items were shown when the activity (fragment) was "unloaded".
     */
//    private boolean mRefreshResultOnResume = false;

    private ArrayList<T> mResult;
    private ViewGroup mList;
    private FrameLayout mProgressView;
    private View mEmptyView;
    private int mEmptyHeaderTextId;
    private int mEmptyMessageTextId;
    private boolean mSearchPending;

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
//        setWillNotDraw(false);
        mEmptyMessageTextId = emptyMessageTextId;
        mEmptyHeaderTextId = emptyHeaderTextId;

        if (isListContentHeight) {
            initFullLengthView(context);
        } else {
            initScrollingList(context);
        }
        mProgressView = (FrameLayout) findViewById(R.id.searchResultProgress);
        mEmptyView = findViewById(R.id.searchResultEmpty);

        showLoadingSpinner();

        initEmptyViewText(R.id.searchResultEmptyHeader, mEmptyHeaderTextId);
        initEmptyViewText(R.id.searchResultEmptyMessage, mEmptyMessageTextId);
    }

    private void showLoadingSpinner() {
        mList.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mProgressView.setVisibility(View.VISIBLE);
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
        mList = linearLayout;
    }

    protected abstract ArrayAdapter<T> createAdapter(List<T> result);

    protected void sort(Comparator<T> sorter) {
        if (mList instanceof ListView) {
            ListView listView = (ListView) mList;
            ((ArrayAdapter<T>) listView.getAdapter()).sort(sorter);
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
    }

    protected void onSearchDone(List<T> result) {
    }

    public void setResult(List<T> result) {
        if (result == null) {
            //TODO: Necessary? Remove?
            result = new ArrayList<T>();
        }
        ArrayAdapter<T> adapter = createAdapter(result);

        mList.setVisibility(adapter.isEmpty() ? View.GONE : View.VISIBLE);
        mEmptyView.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);
        if (mList instanceof ListView) {
            ListView listView = (ListView) mList;
            listView.setAdapter(adapter);
        } else {
            LinearLayout linearLayout = (LinearLayout) mList;
            linearLayout.removeAllViews();
            for (int i = 0; i < adapter.getCount(); i++) {
                View view = adapter.getView(i, null, linearLayout);
                view.setOnClickListener(this);
                view.setFocusable(true);
                view.setClickable(true);
                Drawable backgroundDrawable = getSelectableItemBackgroundDrawable();
                view.setBackgroundDrawable(backgroundDrawable);
                linearLayout.addView(view);
            }
        }

        mResult = new ArrayList<T>(result);

        mProgressView.setVisibility(View.GONE);
        Log.d(ActivitiesListFragment.class.getName(), "Progress view has been hidden and list view has been shown.");

        onSearchDone(result);
        invalidate(); //TODO: Necessary? Remove?
    }

    private Drawable getSelectableItemBackgroundDrawable() {
        // Create an array of the attributes we want to resolve using values from a theme
        int[] attrs = new int[]{android.R.attr.selectableItemBackground /* index 0 */};

        // Obtain the styled attributes. 'themedContext' is a context with a theme, typically the current Activity (i.e. 'this')
        TypedArray ta = getContext().obtainStyledAttributes(attrs);

        // Now get the value of the 'listItemBackground' attribute that was set in the theme used in 'themedContext'. The parameter is the index of the attribute in the 'attrs' array. The returned Drawable is what you are after
        Drawable drawableFromTheme = ta.getDrawable(0 /* index */);

        // Finally free resources used by TypedArray
        ta.recycle();
        return drawableFromTheme;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putSerializable("mResult", mResult);
//        bundle.putBoolean("mRefreshResultOnResume", mRefreshResultOnResume);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mResult = (ArrayList<T>) bundle.getSerializable("mResult");
//            mRefreshResultOnResume = bundle.getBoolean("mRefreshResultOnResume");
            state = bundle.getParcelable("instanceState");

            setResult(mResult);
        }
        super.onRestoreInstanceState(state);
    }

    public abstract SearchTask createSearchTask();

    public void runSearchTaskInNewThread() {
        synchronized (this) {
            if (!mSearchPending) {
                mSearchPending = true;
                createSearchTask().execute();
            } else {
                Log.i(getClass().getName(), "Will not start new thread for updating list since previous search thread has not completed.");
            }
        }
    }

    public boolean isResultPresent() {
        synchronized (this) {
            return /*!mRefreshResultOnResume && */mResult != null;
        }
    }

/*
    protected void invalidateResult() {
        synchronized (this) {
            mRefreshResultOnResume = true;
        }
    }
*/


/*
    @Override
    protected void onDraw(Canvas canvas) {
        if (!isResultPresent()) {
            createSearchTask().execute();
        }
        super.onDraw(canvas);    //To change body of overridden methods use File | Settings | File Templates.
    }
*/

/*
    @Override
    public void onFragmentResume() {
        if (!isResultPresent()) {
            createSearchTask().execute();
        }
    }
*/

    public abstract class SearchTask extends AsyncTask<Object, Object, List<T>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingSpinner();
        }

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
            synchronized (NonBlockingSearchView.this) {
                mSearchPending = false;
            }
        }

        protected abstract List<T> doSearch();

    }
}
