package se.devscout.android.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import se.devscout.android.controller.fragment.GalleryThumbnailsFragment;
import se.devscout.android.model.MediaKey;
import se.devscout.android.model.ObjectIdentifierBean;

public class GalleryThumbnailsActivity extends SingleFragmentActivity {
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

//        getSupportActionBar().hide();
    }

    public static Intent createIntent(Context ctx, List<? extends MediaKey> mediaItems) {
        Intent intent = new Intent(ctx, GalleryThumbnailsActivity.class);
        ArrayList<ObjectIdentifierBean> keys = new ArrayList<ObjectIdentifierBean>();
        for (MediaKey media : mediaItems) {
            keys.add(new ObjectIdentifierBean(media));
        }
        intent.putExtra(INTENT_EXTRA_KEYS, keys);
        return intent;
    }

}
