package se.devscout.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.controller.activity.SearchResultActivity;
import se.devscout.server.api.ActivityFilter;

import java.io.Serializable;
import java.util.List;

public abstract class QuickSearchListView<T extends Serializable> extends NonBlockingSearchView<T> {
    public QuickSearchListView(Context context, AttributeSet attrs, int defStyle, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight, List<T> items) {
        super(context, attrs, defStyle, emptyMessageTextId, emptyHeaderTextId, isListContentHeight);
        mItems = items;
    }

    public QuickSearchListView(Context context, AttributeSet attrs, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight, List<T> items) {
        super(context, attrs, emptyMessageTextId, emptyHeaderTextId, isListContentHeight);
        mItems = items;
    }

    public QuickSearchListView(Context context, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight, List<T> items) {
        super(context, emptyMessageTextId, emptyHeaderTextId, isListContentHeight);
        mItems = items;
    }

    protected List<T> mItems;

/*
    protected QuickSearchListView(T[] items) {
        this(Arrays.asList(items));
    }
*/

    @Override
    public SearchTask createSearchTask() {
        return new MySearchTask();
    }

    protected ArrayAdapter<T> createAdapter(final List<T> result) {
        return new ArrayAdapter<T>(getContext(), android.R.layout.simple_list_item_1, result) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.quick_search_list_item, null);
                }

                T item = result.get(position);
                TextView titleView = (TextView) convertView.findViewById(R.id.quickSearchListItemTitle);
                titleView.setText(getTitle(item));
                TextView subtitleView = (TextView) convertView.findViewById(R.id.quickSearchListItemSubtitle);
                subtitleView.setText(getSubtitle(item));
                ImageView imageView = (ImageView) convertView.findViewById(R.id.quickSearchListItemImage);
                int imageResId = getImageResId(item);
                if (imageResId > 0) {
                    imageView.setImageResource(imageResId);
                }
                imageView.setVisibility(imageResId > 0 ? View.VISIBLE : View.INVISIBLE);
                return convertView;
            }
        };
    }

/*
    protected T getResultObjectFromId(ObjectIdentifierPojo identifier) {
        for (T item : mItems) {
            if (identifier.getId() == item.getId()) {
                return item;
            }
        }
        return null;
    }
*/

    @Override
    public void onItemClick(View view, final int position) {
        T item = getItems().get(position);
        getContext().startActivity(SearchResultActivity.createIntent(getContext(), createFilter(item), getSearchResultTitle(item)));
    }

    protected abstract int getImageResId(T item);

    protected abstract String getTitle(T option);

    protected abstract String getSubtitle(T option);

    protected abstract ActivityFilter createFilter(T option);

    protected abstract String getSearchResultTitle(T option);

    private class MySearchTask extends SearchTask {
        @Override
        protected List<T> doSearch() {
            return mItems;
        }
    }
}
