package se.devscout.android.controller.fragment;

import android.R;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import se.devscout.android.model.repo.SQLiteActivityRepo;
import se.devscout.android.util.IsFeaturedFilter;
import se.devscout.server.api.model.Activity;
import se.devscout.server.api.model.ActivityRevision;

import java.util.List;

public class FeaturedActivitiesListFragment extends ActivitiesListFragment {

    /**
     * No-args constructor necessary when support library restored fragment.
     */
    public FeaturedActivitiesListFragment() {
        this(Sorter.NAME);
    }

    public FeaturedActivitiesListFragment(Sorter sortOrder) {
        super(new IsFeaturedFilter(), sortOrder);
    }

    @Override
    protected ArrayAdapter<Activity> createAdapter(final List<Activity> result) {
        return new ArrayAdapter<Activity>(getActivity(), R.layout.simple_list_item_1, result) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // TODO Use some kind of factory for accessing/creating the ActivityBank instead of forcing SQLiteActivityRepo?
                List<? extends ActivityRevision> revisions = SQLiteActivityRepo.getInstance(getActivity()).read(result.get(position)).getRevisions();
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

    public static FeaturedActivitiesListFragment create(Sorter defaultSortOrder) {
        return new FeaturedActivitiesListFragment(defaultSortOrder);
    }
}
