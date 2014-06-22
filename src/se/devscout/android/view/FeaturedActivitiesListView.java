package se.devscout.android.view;

import android.content.Context;
import android.widget.ArrayAdapter;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.FeaturedActivitiesArrayAdapter;
import se.devscout.android.util.ActivityBankFactory;

import java.util.List;

public class FeaturedActivitiesListView extends ActivitiesListView {

    /**
     * No-args constructor necessary when support library restored fragment.
     */
    public FeaturedActivitiesListView(Context context, boolean isListContentHeight) {
        this(context, Sorter.NAME, isListContentHeight);
    }

    public FeaturedActivitiesListView(Context context, Sorter sortOrder, boolean isListContentHeight) {
        super(context, R.string.featuredEmptyHeader, R.string.featuredEmptyMessage, ActivityBankFactory.getInstance(context).getFilterFactory().createIsFeaturedFilter(), sortOrder, isListContentHeight);
    }

    @Override
    protected ArrayAdapter<ActivitiesListItem> createAdapter(final List<ActivitiesListItem> result) {
        return new FeaturedActivitiesArrayAdapter(getContext(), result);
    }

}
