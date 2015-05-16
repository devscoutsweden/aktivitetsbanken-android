package se.devscout.android.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import se.devscout.android.controller.fragment.AuthUserProfileFragment;

public class UserProfileActivity extends SingleFragmentActivity<AuthUserProfileFragment> {
    @Override
    protected AuthUserProfileFragment createFragment() {
        return AuthUserProfileFragment.create();
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
    }

    public static Intent createIntent(Context ctx) {
        Intent intent = new Intent(ctx, UserProfileActivity.class);
        return intent;
    }
}
