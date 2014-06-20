package se.devscout.android.controller.fragment;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import java.util.List;

public class FeaturedActivitiesArrayAdapter extends ArrayAdapter<ActivitiesListItem> {
//    private final List<Activity> mResult;

    public FeaturedActivitiesArrayAdapter(Context context, List<ActivitiesListItem> result) {
        super(context, R.layout.simple_list_item_1, result);
//        mResult = result;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        List<? extends ActivityRevision> revisions = ActivityBankFactory.getInstance(getContext()).read(getItem(position)).getRevisions();
        View view = ActivityCoverFragment.initListItemView(
                LayoutInflater.from(getContext()),
                parent,
                convertView,
                getItem(position));
        view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getContext().getResources().getDimensionPixelSize(R.dimen.thumbnail_height)));
        return view;
    }
}
