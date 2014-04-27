package se.devscout.android.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import se.devscout.android.controller.fragment.ActivityViewPagerFragment;
import se.devscout.android.controller.fragment.KeyPojo;
import se.devscout.android.model.SQLiteActivityRepo;
import se.devscout.server.api.model.Activity;
import se.devscout.server.api.model.ActivityKey;

public class ActivityActivity extends SingleFragmentActivity {

    private static final String INTENT_EXTRA_ACTIVITY_KEY = "activityKey";

    @Override
    protected Fragment createFragment() {
        KeyPojo key = (KeyPojo) getIntent().getSerializableExtra(INTENT_EXTRA_ACTIVITY_KEY);
        return new ActivityViewPagerFragment(key);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        KeyPojo key = (KeyPojo) getIntent().getSerializableExtra(INTENT_EXTRA_ACTIVITY_KEY);
        if (key != null) {
            Activity activity = SQLiteActivityRepo.getInstance(this).read(key);
            setTitle(activity.getRevisions().get(activity.getRevisions().size() - 1).getName());
        }
    }

    public static Intent createIntent(Context ctx, ActivityKey key) {
        Intent intent = new Intent(ctx, ActivityActivity.class);
        intent.putExtra(INTENT_EXTRA_ACTIVITY_KEY, new KeyPojo(key.getId()));
        return intent;
    }

}
