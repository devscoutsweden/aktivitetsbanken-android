package se.devscout.android.controller.fragment;

import android.R;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import se.devscout.android.model.repo.SQLiteActivityRepo;
import se.devscout.android.util.IsFeaturedFilter;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.Activity;
import se.devscout.server.api.model.ActivityRevision;

import java.util.List;

public class FeaturedActivitiesListFragment extends ActivitiesListFragment {

    public FeaturedActivitiesListFragment() {
    }

    public FeaturedActivitiesListFragment(ActivityFilter filter, Sorter sortOrder) {
        super(filter, sortOrder);
    }

    @Override
    protected ArrayAdapter<Activity> createAdapter() {
        return new ArrayAdapter<Activity>(getActivity(), R.layout.simple_list_item_1, getActivities()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // TODO Use some kind of factory for accessing/creating the ActivityBank instead of forcing SQLiteActivityRepo?
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

/*
    public static FeaturedActivitiesListFragment create(ActivityBank repo) {
        FeaturedActivitiesListFragment fragment = new FeaturedActivitiesListFragment();
        ArrayList<Activity> activities = new ArrayList<Activity>(repo.find(new IsFeaturedFilter()));
        fragment.mActivities = new ArrayList<ObjectIdentifierPojo>();
        for (Activity activity : activities) {
            fragment.mActivities.add(new ObjectIdentifierPojo(activity.getId()));
        }
        return fragment;
    }
*/

    public static FeaturedActivitiesListFragment create(Sorter defaultSortOrder) {
        return new FeaturedActivitiesListFragment(new IsFeaturedFilter(), defaultSortOrder);
    }
}
