package se.devscout.android.view;

import android.widget.ArrayAdapter;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.controller.fragment.FeaturedActivitiesArrayAdapter;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.view.widget.FragmentListener;
import se.devscout.server.api.ActivityBankListener;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.SearchHistory;
import se.devscout.server.api.model.UserKey;

import java.util.List;

public class FavouriteActivitiesListView extends ActivitiesListView implements ActivityBankListener, FragmentListener {

    private ActivityBankFragment mFragment;

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
                ActivityBankFactory.getInstance(context.getActivity()).getFilterFactory().createIsUserFavouriteFilter(null),
                sortOrder,
                isListContentHeight);
//        ActivityBankFactory.getInstance(context.getActivity()).addListener(this);
        mFragment = context;
    }

    @Override
    protected void onAttachedToWindow() {
        //TODO: Useless. Remove!?
        mFragment.addListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        //TODO: Useless. Remove!?
        mFragment.removeListener(this);
    }

    @Override
    public void onSearchHistoryItemAdded(SearchHistory searchHistory) {
    }

    @Override
    public void onFavouriteChange(ActivityKey activityKey, UserKey userKey, boolean isFavouriteNow) {
//        invalidateResult();
    }

    @Override
    protected ArrayAdapter<ActivitiesListItem> createAdapter(final List<ActivitiesListItem> result) {
        return new FeaturedActivitiesArrayAdapter(getContext(), result);
    }

    @Override
    //TODO: Useless. Remove!?
    public void onFragmentResume(boolean refreshResultOnResume) {
        if (!isResultPresent()) {
//            createSearchTask().execute();
        }
    }
}
