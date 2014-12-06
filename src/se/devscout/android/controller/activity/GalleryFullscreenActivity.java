package se.devscout.android.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import se.devscout.android.controller.fragment.GalleryFullsizePagerFragment;
import se.devscout.android.model.ObjectIdentifierBean;

import java.util.ArrayList;
import java.util.List;

public class GalleryFullscreenActivity extends SingleFragmentActivity {
    private static final String INTENT_EXTRA_KEYS = "keys";
//    private static final String INTENT_EXTRA_ACTIVITY_TITLES = "activityTitles";
    private static final String INTENT_EXTRA_SELECTED_INDEX = "selectedIndex";

    @Override
    protected Fragment createFragment() {
        List<ObjectIdentifierBean> keys = (List<ObjectIdentifierBean>) getIntent().getSerializableExtra(INTENT_EXTRA_KEYS);
//        ArrayList<String> titles = (ArrayList<String>) getIntent().getSerializableExtra(INTENT_EXTRA_ACTIVITY_TITLES);
        return GalleryFullsizePagerFragment.create(keys, /*titles, */getIntent().getIntExtra(INTENT_EXTRA_SELECTED_INDEX, 0));
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

    public static Intent createIntent(Context ctx, List<ObjectIdentifierBean> keys, ArrayList<String> titles, int selectedIndex) {
        Intent intent = new Intent(ctx, GalleryFullscreenActivity.class);
        intent.putExtra(INTENT_EXTRA_KEYS, new ArrayList<ObjectIdentifierBean>(keys));
//        intent.putExtra(INTENT_EXTRA_ACTIVITY_TITLES, titles);
        intent.putExtra(INTENT_EXTRA_SELECTED_INDEX, selectedIndex);
        return intent;
    }

}
