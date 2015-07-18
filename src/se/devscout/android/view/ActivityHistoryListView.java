package se.devscout.android.view;

import android.content.Context;
import se.devscout.android.R;
import se.devscout.android.model.Activity;
import se.devscout.android.model.ActivityKey;
import se.devscout.android.model.repo.ActivityBank;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.auth.CredentialsManager;
import se.devscout.android.util.http.UnauthorizedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Component for displaying (searching for) activities which are related to a
 * particular activity.
 */
public class ActivityHistoryListView extends ActivitiesListView {
    private final ActivityKey mActivityKey;
    private List<ActivityKey> mRelatedActivitiesKeys;

    /**
     * @param activityKey           The activity for which we want to find related activities.
     * @param relatedActivitiesKeys Override/replace any preexisting relations with these ones. Specify null if you want to show the currently known/stored relations.
     */
    public ActivityHistoryListView(Context context, ActivityKey activityKey, List<ActivityKey> relatedActivitiesKeys) {
        super(context, R.string.noRecentActivitiesMessage, R.string.noRecentActivitiesHeader, null, null, true);
        mActivityKey = activityKey;
        mRelatedActivitiesKeys = relatedActivitiesKeys;
    }

    @Override
    public SearchTask createSearchTask() {
        return new SearchTask() {

            @Override
            protected List<ActivitiesListItem> doSearch() throws UnauthorizedException {
                ActivityBank activityBank = ActivityBankFactory.getInstance(getContext());
                mStopWatch.logEvent("Acquired ActivityBank");

                List<? extends Activity> activities = activityBank.readActivityHistory(
                        5,
                        CredentialsManager.getInstance(getContext()).getCurrentUser());
                mStopWatch.logEvent("Read recent activities");

                List<ActivitiesListItem> items = new ArrayList<ActivitiesListItem>();
                for (Activity activity : activities) {
                    items.add(new ActivitiesListItem(activity, getContext()));
                }
                mStopWatch.logEvent("Created list items");
                return items;
            }
        };
    }
}
