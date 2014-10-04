package se.devscout.android.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import se.devscout.android.controller.fragment.ActivityTabsFragment;
import se.devscout.android.model.ObjectIdentifierPojo;
import se.devscout.server.api.model.Activity;
import se.devscout.server.api.model.ActivityKey;

public class ActivityActivity extends SingleFragmentActivity {

    private static final String INTENT_EXTRA_ACTIVITY_KEY = "activityKey";

    @Override
    protected Fragment createFragment() {
        ObjectIdentifierPojo key = (ObjectIdentifierPojo) getIntent().getSerializableExtra(INTENT_EXTRA_ACTIVITY_KEY);
        return new ActivityTabsFragment(key);
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
        ObjectIdentifierPojo key = (ObjectIdentifierPojo) getIntent().getSerializableExtra(INTENT_EXTRA_ACTIVITY_KEY);
        if (key != null) {
            Activity activity = getActivityBank().readActivityFull(key);
            setTitle(activity.getName());
        }
    }

    public static Intent createIntent(Context ctx, ActivityKey key) {
        Intent intent = new Intent(ctx, ActivityActivity.class);
        intent.putExtra(INTENT_EXTRA_ACTIVITY_KEY, new ObjectIdentifierPojo(key.getId()));
        return intent;
    }

}
