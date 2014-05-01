package se.devscout.android.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivitiesListFragment;
import se.devscout.android.model.ObjectIdentifierPojo;
import se.devscout.android.model.repo.SQLiteActivityRepo;
import se.devscout.server.api.model.ActivityKey;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SearchResultActivity extends SingleFragmentActivity<ActivitiesListFragment> {

    private static final String INTENT_EXTRA_ACTIVITIES = "activities";
    private static final String TITLE_RES_ID = "title";

    private Map<Integer, ActivitiesListFragment.Sorter> mListSorters = new LinkedHashMap<Integer, ActivitiesListFragment.Sorter>();

    public SearchResultActivity() {
        mListSorters.put(R.id.activitiesListMenuSortByName, ActivitiesListFragment.Sorter.NAME);
        mListSorters.put(R.id.activitiesListMenuSortByParticipants, ActivitiesListFragment.Sorter.PARTICIPANT_COUNT);
        mListSorters.put(R.id.activitiesListMenuSortByTime, ActivitiesListFragment.Sorter.TIME);
        mListSorters.put(R.id.activitiesListMenuSortByAges, ActivitiesListFragment.Sorter.AGES);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activities_list, menu);
        for (Map.Entry<Integer, ActivitiesListFragment.Sorter> entry : mListSorters.entrySet()) {
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
        ArrayList<ObjectIdentifierPojo> keys = (ArrayList<ObjectIdentifierPojo>) getIntent().getSerializableExtra(INTENT_EXTRA_ACTIVITIES);

        List<ActivityKey> activities = new ArrayList<ActivityKey>();
        for (ActivityKey key : keys) {
            activities.add(SQLiteActivityRepo.getInstance(this).read(key));
        }
        return ActivitiesListFragment.create(activities, ActivitiesListFragment.Sorter.NAME);
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

    public static Intent createIntent(Context ctx, List<? extends se.devscout.server.api.model.Activity> activities, String title) {

        ArrayList<ObjectIdentifierPojo> keys = new ArrayList<ObjectIdentifierPojo>();
        for (se.devscout.server.api.model.Activity activity : activities) {
            keys.add(new ObjectIdentifierPojo(activity.getId()));
        }

        Intent intent = new Intent(ctx, SearchResultActivity.class);
        intent.putExtra(INTENT_EXTRA_ACTIVITIES, keys);
        intent.putExtra(TITLE_RES_ID, title);
        return intent;
    }

}