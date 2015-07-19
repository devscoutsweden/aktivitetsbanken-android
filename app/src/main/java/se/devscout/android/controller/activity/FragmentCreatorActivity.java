package se.devscout.android.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

public class FragmentCreatorActivity extends SingleFragmentActivity {

    private static final String INTENT_EXTRA_FRAGMENT_CREATOR = "fragmentCreator";
    private static final String INTENT_EXTRA_ACTIVITY_TITLE = "activityTitle";

    @Override
    protected Fragment createFragment() {
        FragmentCreator key = (FragmentCreator) getIntent().getSerializableExtra(INTENT_EXTRA_FRAGMENT_CREATOR);
        return key.createFragment();
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
        String title = (String) getIntent().getSerializableExtra(INTENT_EXTRA_ACTIVITY_TITLE);
        if (title != null) {
            setTitle(title);
        }
    }

    public static Intent createIntent(Context ctx, FragmentCreator key, String title) {
        Intent intent = new Intent(ctx, FragmentCreatorActivity.class);
        intent.putExtra(INTENT_EXTRA_FRAGMENT_CREATOR, key);
        intent.putExtra(INTENT_EXTRA_ACTIVITY_TITLE, title);
        return intent;
    }

}
