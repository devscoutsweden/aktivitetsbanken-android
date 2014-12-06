package se.devscout.android.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import se.devscout.android.controller.fragment.GalleryThumbnailsFragment;
import se.devscout.android.model.ObjectIdentifierBean;

import java.util.ArrayList;
import java.util.List;

public class ActivityGalleryActivity extends SingleFragmentActivity {
    private static final String INTENT_EXTRA_KEYS = "keys";

    @Override
    protected Fragment createFragment() {
        List<ObjectIdentifierBean> keys = (List<ObjectIdentifierBean>) getIntent().getSerializableExtra(INTENT_EXTRA_KEYS);
        return GalleryThumbnailsFragment.create(keys);
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

        getActionBar().hide();
    }

    public static Intent createIntent(Context ctx, List<ObjectIdentifierBean> keys) {
        Intent intent = new Intent(ctx, ActivityGalleryActivity.class);
        intent.putExtra(INTENT_EXTRA_KEYS, new ArrayList<ObjectIdentifierBean>(keys));
        return intent;
    }

}
