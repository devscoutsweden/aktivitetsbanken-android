package se.devscout.android.view;

import android.content.Context;
import android.widget.ArrayAdapter;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivitiesListFragment;
import se.devscout.android.controller.fragment.ActivitiesListItem;
import se.devscout.android.controller.fragment.FeaturedActivitiesArrayAdapter;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.server.api.ActivityBankListener;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.SearchHistory;
import se.devscout.server.api.model.UserKey;

import java.util.List;

public class FavouriteActivitiesListView extends ActivitiesListView implements ActivityBankListener {

//    private boolean mRefreshResultOnResume = false;

    /**
     * No-args constructor necessary when support library restored fragment.
     */
    public FavouriteActivitiesListView(Context context, boolean isListContentHeight) {
        this(context,
                ActivitiesListFragment.Sorter.NAME,
                isListContentHeight);
    }

    public FavouriteActivitiesListView(Context context, ActivitiesListFragment.Sorter sortOrder, boolean isListContentHeight) {
        super(context,
                R.string.favouritesEmptyHeader,
                R.string.favouritesEmptyMessage,
                ActivityBankFactory.getInstance(context).getFilterFactory().createIsUserFavouriteFilter(null),
                sortOrder,
                isListContentHeight);
        ActivityBankFactory.getInstance(context).addListener(this);
    }

    /*
        @Override
        protected List<Activity> doSearch() {
            ActivityBank activityBank = ActivityBankFactory.getInstance(getContext());
            try {
                return (List<Activity>) activityBank.find(activityBank.getFilterFactory().createIsUserFavouriteFilter(null));
            } catch (ActivityFilterFactoryException e) {
                Log.e(FavouriteActivitiesListView.class.getName(), "Could not create filter.", e);
                return Collections.emptyList();
            }
        }
    */
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
