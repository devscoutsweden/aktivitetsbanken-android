package se.devscout.android.view;

import android.content.Context;
import android.widget.ArrayAdapter;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.FeaturedActivitiesArrayAdapter;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.server.api.ActivityBankListener;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.SearchHistory;
import se.devscout.server.api.model.UserKey;

import java.util.List;

public class FavouriteActivitiesListView extends ActivitiesListView implements ActivityBankListener {

    /**
     * No-args constructor necessary when support library restored fragment.
     */
    public FavouriteActivitiesListView(Context context, boolean isListContentHeight) {
        this(context,
                Sorter.NAME,
                isListContentHeight);
    }

    public FavouriteActivitiesListView(Context context, Sorter sortOrder, boolean isListContentHeight) {
        super(context,
                R.string.favouritesEmptyHeader,
                R.string.favouritesEmptyMessage,
                ActivityBankFactory.getInstance(context).getFilterFactory().createIsUserFavouriteFilter(null),
                sortOrder,
                isListContentHeight);
        ActivityBankFactory.getInstance(context).addListener(this);
    }

    @Override
    public void onSearchHistoryItemAdded(SearchHistory searchHistory) {
    }

    @Override
    public void onFavouriteChange(ActivityKey activityKey, UserKey userKey, boolean isFavouriteNow) {
        synchronized (this) {
            mRefreshResultOnResume = true;
        }
    }

    @Override
    protected ArrayAdapter<ActivitiesListItem> createAdapter(final List<ActivitiesListItem> result) {
        return new FeaturedActivitiesArrayAdapter(getContext(), result);
    }
}
