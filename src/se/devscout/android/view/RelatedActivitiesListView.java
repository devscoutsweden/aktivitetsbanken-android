package se.devscout.android.view;

import android.support.v4.app.FragmentActivity;
import se.devscout.android.R;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.http.UnauthorizedException;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.model.Activity;
import se.devscout.server.api.model.ActivityKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Component for displaying (searching for) activities which are related to a
 * particular activity.
 */
public class RelatedActivitiesListView extends ActivitiesListView {
    private final ActivityKey mActivityKey;
    private List<ActivityKey> mRelatedActivitiesKeys;

    /**
     * @param activityKey           The activity for which we want to find related activities.
     * @param relatedActivitiesKeys Override/replace any preexisting relations with these ones. Specify null if you want to show the currently known/stored relations.
     */
    public RelatedActivitiesListView(FragmentActivity context, ActivityKey activityKey, List<ActivityKey> relatedActivitiesKeys) {
        super(context, R.string.noRelatedActivitiesMessage, R.string.noRelatedActivitiesHeader, null, null, true);
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

                List<Activity> activities = (List<Activity>) activityBank.readRelatedActivities(
                        mActivityKey,
                        mRelatedActivitiesKeys);
                mStopWatch.logEvent("Read related activities");

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
