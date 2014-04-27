package se.devscout.android.controller.fragment;

import android.R;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import se.devscout.android.model.ObjectIdentifierPojo;
import se.devscout.android.model.repo.SQLiteActivityRepo;
import se.devscout.android.util.IsFeaturedFilter;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.model.Activity;
import se.devscout.server.api.model.ActivityRevision;

import java.util.ArrayList;
import java.util.List;

public class FeaturedActivitiesListFragment extends ActivitiesListFragment {
    @Override
    protected ArrayAdapter<Activity> createAdapter() {
        return new ArrayAdapter<Activity>(getActivity(), R.layout.simple_list_item_1, getActivities()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                List<? extends ActivityRevision> revisions = SQLiteActivityRepo.getInstance(getActivity()).read(mActivities.get(position)).getRevisions();
                View view = ActivityCoverFragment.initListItemView(
                        getActivity().getLayoutInflater(),
                        parent,
                        convertView,
                        revisions.get(revisions.size() - 1));
                view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.thumbnail_height)));
                return view;
            }
        };
    }

    public static FeaturedActivitiesListFragment create(ActivityBank repo) {
        FeaturedActivitiesListFragment fragment = new FeaturedActivitiesListFragment();
        ArrayList<Activity> activities = new ArrayList<Activity>(repo.find(new IsFeaturedFilter()));
        fragment.mActivities = new ArrayList<ObjectIdentifierPojo>();
        for (Activity activity : activities) {
            fragment.mActivities.add(new ObjectIdentifierPojo(activity.getId()));
        }
        return fragment;
    }
}
