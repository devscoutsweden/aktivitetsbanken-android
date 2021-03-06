package se.devscout.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.*;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivitiesListFragment;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.StopWatch;
import se.devscout.android.util.UsageLogUtil;
import se.devscout.android.util.http.UnauthorizedException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class NonBlockingSearchView<T extends Serializable> extends FrameLayout implements AdapterView.OnItemClickListener, View.OnClickListener/*, FragmentListener */ {
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
    private int mHttpTimeoutCountRef;

    public NonBlockingSearchView(Context context, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context);
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

        findViewById(R.id.searchResultRetry).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingSpinner();
                runSearchTaskInNewThread();
            }
        });

        showLoadingSpinner();

        initEmptyTextViews(mEmptyHeaderTextId, mEmptyMessageTextId);
    }

    private void initEmptyTextViews(int headerTextId, int messageTextId) {
        initEmptyViewText(R.id.searchResultEmptyHeader, headerTextId);
        initEmptyViewText(R.id.searchResultEmptyMessage, messageTextId);
    }

    private void showLoadingSpinner() {
        mList.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mProgressView.setVisibility(View.VISIBLE);
        findViewById(R.id.searchResultRetry).setVisibility(GONE);
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
            LogUtil.e(NonBlockingSearchView.class.getName(), "Cannot only sort ListView");
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
        if (mList instanceof ListView) {
            // Return copy of, potentially sorted, list
            ArrayAdapter<T> source = (ArrayAdapter<T>) ((ListView) mList).getAdapter();
            ArrayList<T> list = new ArrayList<>();
            for (int i = 0; i < source.getCount(); i++) {
                list.add(source.getItem(i));
            }
            return list;
        } else {
            return mResult;
        }
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
        if (UsageLogUtil.getInstance().getHttpTimeouts() > mHttpTimeoutCountRef) {
            // At least one http timeout has occurred since the search was initiated. This timeout MAY have caused problems to the current search and the user should be given the option to restart the search operation.
//            initEmptyTextViews(R.string.searchResultErrorTitle, R.string.searchResultErrorMessage);
            findViewById(R.id.searchResultRetry).setVisibility(VISIBLE);
        } else {
//            initEmptyTextViews(mEmptyHeaderTextId, mEmptyMessageTextId);
            findViewById(R.id.searchResultRetry).setVisibility(GONE);
        }
        if (result == null) {
            //TODO: Necessary? Remove?
            result = new ArrayList<T>();
        }
        ArrayAdapter adapter = createAdapter(result);

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
        LogUtil.d(ActivitiesListFragment.class.getName(), "Progress view has been hidden and list view has been shown.");

        onSearchDone(result);
//        invalidate(); //TODO: Necessary? Remove?
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
        bundle.putInt("mHttpTimeoutCountRef", mHttpTimeoutCountRef);
//        bundle.putBoolean("mRefreshResultOnResume", mRefreshResultOnResume);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mResult = (ArrayList<T>) bundle.getSerializable("mResult");
            mHttpTimeoutCountRef = bundle.getInt("mHttpTimeoutCountRef");
//            mRefreshResultOnResume = bundle.getBoolean("mRefreshResultOnResume");
            state = bundle.getParcelable("instanceState");

            setResult(mResult);
        }
        super.onRestoreInstanceState(state);
    }

    public abstract SearchTask createSearchTask();

    public void runSearchTaskInNewThread() {
        synchronized (this) {
            mHttpTimeoutCountRef = UsageLogUtil.getInstance().getHttpTimeouts();
            if (!mSearchPending) {
                mSearchPending = true;
                createSearchTask().execute();
            } else {
                LogUtil.i(getClass().getName(), "Will not start new thread for updating list since previous search thread has not completed.");
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

    private class SearchTaskResult {
        private Throwable mThrowable;
        private List<T> mResult;

        private SearchTaskResult(List<T> result) {
            mResult = result;
        }

        private SearchTaskResult(Throwable throwable) {
            mThrowable = throwable;
        }
    }

    public abstract class SearchTask extends AsyncTask<Object, Object, SearchTaskResult> {

        protected final StopWatch mStopWatch;

        protected SearchTask() {
            mStopWatch = new StopWatch("SearchTask");
            LogUtil.initExceptionLogging(getContext());
            mStopWatch.logEvent("initExceptionLogging done");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mStopWatch.logEvent("Start of onPreExecute");
            showLoadingSpinner();
            mStopWatch.logEvent("End of onPreExecute");
        }

        @Override
        protected SearchTaskResult doInBackground(Object... voids) {
            LogUtil.d(NonBlockingSearchView.class.getName(), "Start of doInBackground as part of " + NonBlockingSearchView.this.getClass().getName());
            List<T> list = null;
            try {
                mStopWatch.logEvent("Start of doSearch");
                list = doSearch();
            } catch (UnauthorizedException e) {
                LogUtil.i(NonBlockingSearchView.class.getName(), "Could not complete search due to authorization problem.", e);
                return new SearchTaskResult(e);
            } catch (Throwable e) {
                LogUtil.e(NonBlockingSearchView.class.getName(), "Could not complete search due to unexpected problem.", e);
                return new SearchTaskResult(e);
            } finally {
                mStopWatch.logEvent("End of doSearch");
            }
            LogUtil.d(NonBlockingSearchView.class.getName(), "End of doInBackground as part of " + NonBlockingSearchView.this.getClass().getName());
            SearchTaskResult result = new SearchTaskResult(list);
            return result;
        }

        @Override
        protected void onPostExecute(SearchTaskResult result) {
            mStopWatch.logEvent("Start of onPostExecute");
            LogUtil.d(NonBlockingSearchView.class.getName(), "Search task has completed. ");
            if (result != null) {
                if (result.mResult != null) {
                    LogUtil.d(NonBlockingSearchView.class.getName(), result.mResult.size() + " items were returned.");
                    setResult(result.mResult);
                } else {
//                    if (result.mThrowable instanceof UnauthorizedException) {
//                        LogUtil.d(NonBlockingSearchView.class.getName(), "The search failed because of an authorization problem. Assume that this is because the API key was either missing or incorrect. The 'create anonymous user process' will be started and the result will be set to an empty list.");
//                        UnauthorizedException exception = (UnauthorizedException) result.mThrowable;
//                        AnonymousUserFactory factory = AnonymousUserFactory.getInstance();
//                        AnonymousUserFactoryListener callback = new AnonymousUserFactoryListener() {
//                            @Override
//                            public void onAnonymousUserCreated(boolean success) {
//                                runSearchTaskInNewThread();
//                            }
//                        };
//
//                        factory.createAnonymousUser(callback, NonBlockingSearchView.this.getContext());
//                    } else {
                    if (result.mThrowable != null) {
                        LogUtil.e(NonBlockingSearchView.class.getName(), "Something went wrong when searching (an exception was thrown). The result will be set to an empty list.", result.mThrowable);
                        Toast.makeText(getContext(), result.mThrowable.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        LogUtil.d(NonBlockingSearchView.class.getName(), "Something went wrong when searching since the result was 'null'. The result will be set to an empty list.");
                    }
//                    }
                    setResult(Collections.<T>emptyList());
                }
            } else {
                setResult(Collections.<T>emptyList());
            }

            synchronized (NonBlockingSearchView.this) {
                mSearchPending = false;
            }
            mStopWatch.logEvent("End of onPostExecute");
            LogUtil.d(NonBlockingSearchView.class.getName(), mStopWatch.getSummary());
        }

        protected abstract List<T> doSearch() throws UnauthorizedException;

    }
}
