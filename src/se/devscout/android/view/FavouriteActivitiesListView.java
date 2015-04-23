package se.devscout.android.view;

import android.widget.ArrayAdapter;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.controller.fragment.AsyncImageArrayAdapter;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.auth.CredentialsManager;

import java.util.List;

public class FavouriteActivitiesListView extends ActivitiesListView {

    /**
     * No-args constructor necessary when support library restored fragment.
     */
    public FavouriteActivitiesListView(ActivityBankFragment context, boolean isListContentHeight) {
        this(context,
                Sorter.NAME,
                isListContentHeight);
    }

    public FavouriteActivitiesListView(ActivityBankFragment context, Sorter sortOrder, boolean isListContentHeight) {
        super(context.getActivity(),
                R.string.favouritesEmptyHeader,
                R.string.favouritesEmptyMessage,
                ActivityBankFactory.getInstance(context.getActivity()).getFilterFactory().createIsUserFavouriteFilter(CredentialsManager.getInstance(context.getActivity()).getCurrentUser()),
                sortOrder,
                isListContentHeight);
    }

    @Override
    protected ArrayAdapter<ActivitiesListItem> createAdapter(final List<ActivitiesListItem> result) {
        return new AsyncImageArrayAdapter(getContext(), result);
    }

}
