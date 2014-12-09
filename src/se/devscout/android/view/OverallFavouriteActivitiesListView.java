package se.devscout.android.view;

import android.widget.ArrayAdapter;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.controller.fragment.AsyncImageArrayAdapter;
import se.devscout.android.util.ActivityBankFactory;

import java.util.List;

public class OverallFavouriteActivitiesListView extends ActivitiesListView {

    /**
     * No-args constructor necessary when support library restored fragment.
     */
    public OverallFavouriteActivitiesListView(ActivityBankFragment context, boolean isListContentHeight) {
        this(context,
                Sorter.FAVOURITE_COUNT,
                isListContentHeight);
    }

    public OverallFavouriteActivitiesListView(ActivityBankFragment context, Sorter sortOrder, boolean isListContentHeight) {
        super(context.getActivity(),
                R.string.favouritesEmptyHeader,
                R.string.favouritesEmptyMessage,
                ActivityBankFactory.getInstance(context.getActivity()).getFilterFactory().createOverallFavouriteActivitiesFilter(5),
                sortOrder,
                isListContentHeight);
    }

    @Override
    protected ArrayAdapter createAdapter(final List<ActivitiesListItem> result) {
        return AsyncImageArrayAdapter.fromList(result, getContext(), getWidth());
    }

}
