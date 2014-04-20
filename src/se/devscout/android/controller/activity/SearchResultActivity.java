package se.devscout.android.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import se.devscout.android.controller.fragment.ActivitiesListFragment;
import se.devscout.android.model.LocalActivity;

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
        ArrayList<LocalActivity> activities = (ArrayList<LocalActivity>) getIntent().getSerializableExtra(INTENT_EXTRA_ACTIVITIES);

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

    public static Intent createIntent(Context ctx, List<LocalActivity> activities, String title) {
        Intent intent = new Intent(ctx, SearchResultActivity.class);
        intent.putExtra(INTENT_EXTRA_ACTIVITIES, new ArrayList<LocalActivity>(activities));
        intent.putExtra(TITLE_RES_ID, title);
        return intent;
    }

}