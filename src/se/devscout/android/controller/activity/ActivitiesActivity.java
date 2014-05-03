package se.devscout.android.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import se.devscout.android.controller.fragment.ActivitiesViewPagerFragment;
import se.devscout.android.model.ObjectIdentifierPojo;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesActivity extends SingleFragmentActivity {
    private static final String INTENT_EXTRA_ACTIVITY_KEY = "activityKeys";
    private static final String INTENT_EXTRA_SELECTED_INDEX = "selectedIndex";

    @Override
    protected Fragment createFragment() {
        List<ObjectIdentifierPojo> keys = (List<ObjectIdentifierPojo>) getIntent().getSerializableExtra(INTENT_EXTRA_ACTIVITY_KEY);
        return ActivitiesViewPagerFragment.create(keys, getIntent().getIntExtra(INTENT_EXTRA_SELECTED_INDEX, 0));
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
/*
        ObjectIdentifierPojo key = (ObjectIdentifierPojo) getIntent().getSerializableExtra(INTENT_EXTRA_ACTIVITY_KEY);
        if (key != null) {
            // TODO Use some kind of factory for accessing/creating the ActivityBank instead of forcing SQLiteActivityRepo?
            Activity activity = SQLiteActivityRepo.getInstance(this).read(key);
            setTitle(activity.getRevisions().get(activity.getRevisions().size() - 1).getName());
        }
*/
    }

    public static Intent createIntent(Context ctx, List<ObjectIdentifierPojo> keys, int selectedIndex) {
        Intent intent = new Intent(ctx, ActivitiesActivity.class);
        intent.putExtra(INTENT_EXTRA_ACTIVITY_KEY, new ArrayList<ObjectIdentifierPojo>(keys));
        intent.putExtra(INTENT_EXTRA_SELECTED_INDEX, selectedIndex);
        return intent;
    }

}
