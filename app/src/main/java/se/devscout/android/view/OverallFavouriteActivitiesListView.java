package se.devscout.android.view;

import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.util.ActivityBankFactory;

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
                R.string.overallFavouritesEmptyHeader,
                R.string.overallFavouritesEmptyMessage,
                ActivityBankFactory.getInstance(context.getActivity()).getFilterFactory().createOverallFavouriteActivitiesFilter(5),
                sortOrder,
                isListContentHeight);
    }

}
