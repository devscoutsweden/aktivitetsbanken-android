package se.devscout.android.view;

import android.content.Context;
import se.devscout.android.R;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.auth.CredentialsManager;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.model.Activity;

import java.util.ArrayList;
import java.util.List;

public class RecentActivitiesListView extends ActivitiesListView {

    public RecentActivitiesListView(Context context, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context, R.string.noRecentActivitiesMessage, R.string.noRecentActivitiesHeader, null, null, true);
    }

    @Override
    public SearchTask createSearchTask() {
        return new SearchHistorySearchTask();
    }

    private class SearchHistorySearchTask extends SearchTask {
        @Override
        protected List<ActivitiesListItem> doSearch() {
            ActivityBank activityBank = ActivityBankFactory.getInstance(getContext());
            mStopWatch.logEvent("Acquired ActivityBank");
            List<? extends Activity> activities = activityBank.readActivityHistory(5, CredentialsManager.getInstance(getContext()).getCurrentUser());
            List<ActivitiesListItem> items = new ArrayList<ActivitiesListItem>();
            for (Activity activity : activities) {
                items.add(new ActivitiesListItem(activity, getContext()));
            }
            mStopWatch.logEvent("Created list items");
            return items;
        }
    }

}
