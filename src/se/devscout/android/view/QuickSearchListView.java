package se.devscout.android.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.controller.activity.SearchResultActivity;
import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.server.api.ActivityFilter;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

public abstract class QuickSearchListView<T extends Serializable> extends NonBlockingSearchView<T> {

    public QuickSearchListView(Context context, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight, List<T> items) {
        super(context, emptyMessageTextId, emptyHeaderTextId, isListContentHeight);
        mItems = items;
    }

    protected List<T> mItems;

    @Override
    public SearchTask createSearchTask() {
        return new MySearchTask();
    }

    protected ArrayAdapter<T> createAdapter(List<T> result) {
        return new ArrayAdapter<T>(getContext(), android.R.layout.simple_list_item_1, result) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.quick_search_list_item, null);
                }

                T item = getItem(position);
                TextView titleView = (TextView) convertView.findViewById(R.id.quickSearchListItemTitle);
                titleView.setText(getTitle(item));
                TextView subtitleView = (TextView) convertView.findViewById(R.id.quickSearchListItemSubtitle);
                subtitleView.setText(getSubtitle(item));
                AsyncImageView imageView = (AsyncImageView) convertView.findViewById(R.id.quickSearchListItemImage);
                int imageResId = getImageResId(item);
                URI imageURI = getImageURI(item);
                if (imageResId > 0) {
                    imageView.setImage(imageResId);
                }
                if (imageURI != null) {
                    URI[] uris = {imageURI};
//                        int limitSmall = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("server_download_limit_small", "10")) * 1000;
                    imageView.setImage(new AsyncImageBean(null, uris), ((SingleFragmentActivity) getContext()).getBackgroundTasksHandlerThread());
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
                imageView.setVisibility(imageResId > 0 ? View.VISIBLE : View.INVISIBLE);
                return convertView;
            }
        };
    }

    @Override
    public void onItemClick(View view, final int position) {
        T item = getItems().get(position);
        getContext().startActivity(SearchResultActivity.createIntent(getContext(), createFilter(item), getSearchResultTitle(item)));
    }

    protected abstract URI getImageURI(T item);

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
