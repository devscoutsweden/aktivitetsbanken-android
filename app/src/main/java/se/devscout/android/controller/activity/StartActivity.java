package se.devscout.android.controller.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import se.devscout.android.BuildConfig;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.StartTabsFragment;
import se.devscout.android.model.repo.sql.SQLiteActivityRepo;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.PreferencesUtil;
import se.devscout.android.util.UsageLogUtil;
import se.devscout.android.util.auth.CredentialsManager;
import se.devscout.android.util.concurrency.CleanImageCacheTaskExecutor;

public class StartActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new StartTabsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuStartSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.menuStartShowProfile:
                startActivity(UserProfileActivity.createIntent(StartActivity.this));
                break;
            case R.id.menuStartResetDismissedMessages:
                PreferencesUtil.resetAllDismissedMessages(StartActivity.this);
                break;
            case R.id.menuStartClearImageCache:
                new CleanImageCacheTaskExecutor(0, 0).run(null, this);
                Toast.makeText(this, R.string.clearImageCacheDone, Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuStartShowUsageStats:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(StartActivity.this);
                UsageLogUtil usageLogUtil = UsageLogUtil.getInstance();
                builder1.setMessage("" +
                        "Default server:\n" + BuildConfig.SERVER_API_HOST + "\n\n" +
                        "Version name:\n" + BuildConfig.VERSION_NAME + "\n\n" +
                        "Version id: " + BuildConfig.VERSION_CODE + "\n" +
                        "HTTP requests: " + usageLogUtil.getHttpRequestCount() + "\n" +
                        "HTTP timeouts: " + usageLogUtil.getHttpTimeouts() + "\n" +
                        "Data received: " + usageLogUtil.getHttpBytesRead() + " bytes");
                builder1.show();
                break;
            case R.id.menuStartResetDatabase:
                LogUtil.i(StartActivity.class.getName(), "Resetting database.");
                AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                AlertDialog dialog = builder.setMessage(R.string.resetDatabaseConfirmationMessage).setPositiveButton(R.string.resetDatabaseConfirmationOk, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        SQLiteActivityRepo.getInstance(StartActivity.this).resetDatabase(false);
                    }
                }).setNegativeButton(R.string.resetDatabaseConfirmationCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
                dialog.show();
                LogUtil.i(StartActivity.class.getName(), "Database has been reset.");
                break;
            case R.id.menuStartLogOutAndRevokeAccess:
                CredentialsManager cm = CredentialsManager.getInstance(this);
                if (cm.getState().isLoggedIn()) {
                    cm.logOut(this, true);
                } else {
                    Toast.makeText(this, R.string.auth_cannot_revoke_since_not_logged_in, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        MenuItem item = menu.findItem(R.id.menuStartLogOutAndRevokeAccess);
        if (item != null) {
            item.setVisible(CredentialsManager.getInstance(this).getState().isLoggedIn());
        } else {
            LogUtil.e(StartActivity.class.getName(), "Could not find menu item menuStartLogOutAndRevokeAccess");
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean b = super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.start_options, menu);
        return b;
    }

}