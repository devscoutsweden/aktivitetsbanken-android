package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import se.devscout.android.DemoActivityRepo;
import se.devscout.android.R;
import se.devscout.android.controller.activity.SearchResultActivity;
import se.devscout.android.model.LocalActivity;
import se.devscout.server.api.ActivityFilter;

import java.util.Arrays;
import java.util.List;

abstract class QuickSearchListFragment<T> extends ListFragment {

    private List<T> mItems;
    protected ArrayAdapter<T> mListAdapter;

    protected QuickSearchListFragment(List<T> items) {
        mItems = items;
    }

    protected QuickSearchListFragment(T[] items) {
        this(Arrays.asList(items));
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        T item = mListAdapter.getItem(position);
        List<LocalActivity> activities = DemoActivityRepo.getInstance(getActivity()).find(createFilter(item));
        startActivity(SearchResultActivity.createIntent(getActivity(), activities, getSearchResultTitle(item)));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListAdapter = new ArrayAdapter<T>(getActivity(), android.R.layout.simple_list_item_1, mItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.quick_search_list_item, null);
                }

                T item = mItems.get(position);
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
        setListAdapter(mListAdapter);
    }

    protected abstract int getImageResId(T item);

    protected abstract String getTitle(T option);

    protected abstract String getSubtitle(T option);

    protected abstract ActivityFilter createFilter(T option);

    protected abstract String getSearchResultTitle(T option);

}
