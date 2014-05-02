package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import se.devscout.android.R;

public class SearchResultFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_result, container, false);

        ListView list = (ListView) view.findViewById(R.id.searchResultList);
        list.setVisibility(View.INVISIBLE);
        return view;
    }

    public SearchResultFragment create() {
        return new SearchResultFragment();
    }
}
