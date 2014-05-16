package se.devscout.android.controller.fragment;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.server.api.model.Activity;
import se.devscout.server.api.model.ActivityRevision;

import java.util.List;

class FeaturedActivitiesArrayAdapter extends ArrayAdapter<Activity> {
    private final List<Activity> mResult;

    public FeaturedActivitiesArrayAdapter(Context context, List<Activity> result) {
        super(context, R.layout.simple_list_item_1, result);
        mResult = result;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Use some kind of factory for accessing/creating the ActivityBank instead of forcing SQLiteActivityRepo?
        List<? extends ActivityRevision> revisions = ActivityBankFactory.getInstance(getContext()).read(mResult.get(position)).getRevisions();
        View view = ActivityCoverFragment.initListItemView(
                LayoutInflater.from(getContext()),
                parent,
                convertView,
                revisions.get(revisions.size() - 1));
        view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getContext().getResources().getDimensionPixelSize(R.dimen.thumbnail_height)));
        return view;
    }
}
