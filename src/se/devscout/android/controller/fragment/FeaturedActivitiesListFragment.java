package se.devscout.android.controller.fragment;

import android.R;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import se.devscout.android.DemoActivityRepo;
import se.devscout.android.model.LocalActivity;
import se.devscout.android.util.IsFeaturedFilter;

import java.util.ArrayList;

public class FeaturedActivitiesListFragment extends ActivitiesListFragment {
    @Override
    protected ArrayAdapter<LocalActivity> createAdapter() {
        return new ArrayAdapter<LocalActivity>(getActivity(), R.layout.simple_list_item_1, mActivities) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = ActivityCoverFragment.initListItemView(
                        getActivity().getLayoutInflater(),
                        parent,
                        convertView,
                        mActivities.get(position));
                view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.thumbnail_height)));
                return view;
            }
        };
    }

    public static FeaturedActivitiesListFragment create(DemoActivityRepo repo) {
        FeaturedActivitiesListFragment fragment = new FeaturedActivitiesListFragment();
        fragment.mActivities = new ArrayList<LocalActivity>(repo.find(new IsFeaturedFilter()));
        return fragment;
    }
}
