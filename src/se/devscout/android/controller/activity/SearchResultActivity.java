package se.devscout.android.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivitiesListFragment;
import se.devscout.android.model.ObjectIdentifierBean;
import se.devscout.android.view.ActivitiesListView;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.ActivityKey;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SearchResultActivity extends SingleFragmentActivity<ActivitiesListFragment> {

    private static final String INTENT_EXTRA_ACTIVITIES = "activities";
    private static final String INTENT_EXTRA_FILTER = "filter";
    private static final String TITLE_RES_ID = "title";

    private Map<Integer, ActivitiesListView.Sorter> mListSorters = new LinkedHashMap<Integer, ActivitiesListView.Sorter>();

    public SearchResultActivity() {
        mListSorters.put(R.id.activitiesListMenuSortByName, ActivitiesListView.Sorter.NAME);
        mListSorters.put(R.id.activitiesListMenuSortByParticipants, ActivitiesListView.Sorter.PARTICIPANT_COUNT);
        mListSorters.put(R.id.activitiesListMenuSortByTime, ActivitiesListView.Sorter.TIME);
        mListSorters.put(R.id.activitiesListMenuSortByAges, ActivitiesListView.Sorter.AGES);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activities_list, menu);
        for (Map.Entry<Integer, ActivitiesListView.Sorter> entry : mListSorters.entrySet()) {
            if (entry.getValue() == mFragment.getSortOrder()) {
                menu.findItem(entry.getKey()).setChecked(true);
                break;
            }
        }
        return result;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getIntent().getStringExtra(TITLE_RES_ID));
    }

    @Override
    protected ActivitiesListFragment createFragment() {
        ArrayList<ObjectIdentifierBean> keys = (ArrayList<ObjectIdentifierBean>) getIntent().getSerializableExtra(INTENT_EXTRA_ACTIVITIES);
        if (keys != null) {
            List<ActivityKey> activities = new ArrayList<ActivityKey>();
            for (ActivityKey key : keys) {
                activities.add(getActivityBank().readActivityFull(key));
            }
            return ActivitiesListFragment.create(activities, ActivitiesListView.Sorter.NAME);
        } else {
            ActivityFilter filter = (ActivityFilter) getIntent().getSerializableExtra(INTENT_EXTRA_FILTER);
            if (filter != null) {
                return ActivitiesListFragment.create(filter, ActivitiesListView.Sorter.NAME);
            }
            throw new IllegalArgumentException("Neither activities nor filter specified when starting activity.");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.activitiesListMenuSortByName:
            case R.id.activitiesListMenuSortByParticipants:
            case R.id.activitiesListMenuSortByTime:
            case R.id.activitiesListMenuSortByAges:
                mFragment.setSortOrder(mListSorters.get(item.getItemId()));
                item.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Create intent for starting search result activity with a specific set of activities (activities which have
     * already been loaded/fetched from the database).
     *
     * @param ctx
     * @param activities
     * @param title
     * @return
     */
    public static Intent createIntent(Context ctx, List<? extends se.devscout.server.api.model.Activity> activities, String title) {

        ArrayList<ObjectIdentifierBean> keys = new ArrayList<ObjectIdentifierBean>();
        for (se.devscout.server.api.model.Activity activity : activities) {
            keys.add(new ObjectIdentifierBean(activity.getId()));
        }

        Intent intent = new Intent(ctx, SearchResultActivity.class);
        intent.putExtra(INTENT_EXTRA_ACTIVITIES, keys);
        intent.putExtra(TITLE_RES_ID, title);
        return intent;
    }

    /**
     * Create intent for starting search result activity with any activities matching a supplied filter. This forces
     * the result activity to start off by querying the database for a list of activities.
     *
     * @param ctx
     * @param filter
     * @param title
     * @return
     */
    public static Intent createIntent(Context ctx, ActivityFilter filter, String title) {
        Intent intent = new Intent(ctx, SearchResultActivity.class);
        intent.putExtra(INTENT_EXTRA_FILTER, filter);
        intent.putExtra(TITLE_RES_ID, title);
        return intent;
    }

}