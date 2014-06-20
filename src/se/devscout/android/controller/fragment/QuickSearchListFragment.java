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

    protected List<T> doSearch() {
        return mItems;
    }

/*
    protected ArrayAdapter<T> createAdapter(final List<T> result) {
        return new ArrayAdapter<T>(getActivity(), android.R.layout.simple_list_item_1, result) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.quick_search_list_item, null);
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
*/

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

/*
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
        T item = getListAdapter().getItem(position);
        startActivity(SearchResultActivity.createIntent(getActivity(), createFilter(item), getSearchResultTitle(item)));
    }
*/

//    protected abstract int getImageResId(T item);

//    protected abstract String getTitle(T option);

//    protected abstract String getSubtitle(T option);

//    protected abstract ActivityFilter createFilter(T option);

//    protected abstract String getSearchResultTitle(T option);

}
