package se.devscout.android.view;

import android.widget.ArrayAdapter;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.controller.fragment.AsyncImageArrayAdapter;
import se.devscout.android.util.ActivityBankFactory;

import java.util.List;

public class FeaturedActivitiesListView extends ActivitiesListView {

    /**
     * No-args constructor necessary when support library restored fragment.
     */
    public FeaturedActivitiesListView(ActivityBankFragment context, boolean isListContentHeight) {
        this(context,
                Sorter.NAME,
                isListContentHeight);
    }

    public FeaturedActivitiesListView(ActivityBankFragment context, Sorter sortOrder, boolean isListContentHeight) {
        super(context.getActivity(),
                R.string.featuredEmptyHeader,
                R.string.featuredEmptyMessage,
                ActivityBankFactory.getInstance(context.getActivity()).getFilterFactory().createIsFeaturedFilter(),
                sortOrder,
                isListContentHeight);
    }

    @Override
    protected ArrayAdapter<ActivitiesListItem> createAdapter(final List<ActivitiesListItem> result) {
        return new AsyncImageArrayAdapter(getContext(), result);
    }

}
