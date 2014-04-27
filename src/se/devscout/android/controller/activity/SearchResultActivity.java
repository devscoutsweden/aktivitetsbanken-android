package se.devscout.android.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import se.devscout.android.controller.fragment.ActivitiesListFragment;
import se.devscout.android.model.ObjectIdentifierPojo;
import se.devscout.android.model.repo.SQLiteActivityRepo;
import se.devscout.server.api.model.ActivityKey;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends SingleFragmentActivity {

    private static final String INTENT_EXTRA_ACTIVITIES = "activities";
    private static final String TITLE_RES_ID = "title";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getIntent().getStringExtra(TITLE_RES_ID));
    }

    @Override
    protected Fragment createFragment() {
        ArrayList<ObjectIdentifierPojo> keys = (ArrayList<ObjectIdentifierPojo>) getIntent().getSerializableExtra(INTENT_EXTRA_ACTIVITIES);


        List<ActivityKey> activities = new ArrayList<ActivityKey>();
        for (ActivityKey key : keys) {
            activities.add(SQLiteActivityRepo.getInstance(this).read(key));
        }
        return ActivitiesListFragment.create(activities);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
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