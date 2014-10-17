package se.devscout.android.controller.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.StartTabsFragment;
import se.devscout.android.model.repo.sql.SQLiteActivityRepo;
import se.devscout.android.util.LogUtil;

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
        switch (item.getItemId()) {
            case R.id.menuStartResetDatabase:
                LogUtil.i(StartActivity.class.getName(), "Resetting database.");
                AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                AlertDialog dialog = builder.setMessage("Create test data?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        SQLiteActivityRepo.getInstance(StartActivity.this).resetDatabase(true);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        SQLiteActivityRepo.getInstance(StartActivity.this).resetDatabase(false);
                    }
                }).create();
                dialog.show();
                LogUtil.i(StartActivity.class.getName(), "Database has been reset.");
                break;
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