package se.devscout.android.controller.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.StartTabsFragment;
import se.devscout.android.model.repo.sql.SQLiteActivityRepo;

public class StartActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new StartTabsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.menuStartResetDatabase) {
            Log.i(StartActivity.class.getName(), "Resetting database.");
            SQLiteActivityRepo.getInstance(this).resetDatabase(true);
            Log.i(StartActivity.class.getName(), "Database has been reset.");
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean b = super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.start_options, menu);
        return b;
    }

}