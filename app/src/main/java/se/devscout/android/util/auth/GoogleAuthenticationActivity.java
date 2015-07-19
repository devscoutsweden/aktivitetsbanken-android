package se.devscout.android.util.auth;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import se.devscout.android.R;
import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.android.controller.fragment.AuthProgressFragment;
import se.devscout.android.model.UserPropertiesBean;
import se.devscout.android.model.repo.remote.ServerAPICredentials;
import se.devscout.android.util.IdentityProvider;
import se.devscout.android.util.LogUtil;

public class GoogleAuthenticationActivity extends SingleFragmentActivity<AuthProgressFragment> implements /*AuthenticationStrategy, */GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static enum Action {
        LOG_IN,
        LOG_OUT,
        LOG_OUT_AND_REVOKE
    }

    @Override
    protected AuthProgressFragment createFragment() {
        return AuthProgressFragment.create();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mCredentialsManager.onLogInCancelled();
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

    public static Intent createLogInIntent(Context ctx) {
        Intent intent = new Intent(ctx, GoogleAuthenticationActivity.class);
        intent.putExtra("mAction", Action.LOG_IN.name());
        return intent;
    }

    public static Intent createLogOutIntent(Context ctx, boolean revokeAccess) {
        Intent intent = new Intent(ctx, GoogleAuthenticationActivity.class);
        intent.putExtra("mAction", revokeAccess ? Action.LOG_OUT_AND_REVOKE.name() : Action.LOG_OUT.name());
        return intent;
    }

    private static final int RC_SIGN_IN = 0;
    private static final int RC_CHOOSE_ACCOUNT = 1000;
    private static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
    private static final String WEB_APP_CLIENT_ID = "551713736410-q55omfobgs9j8ia4ae3r7sbi20vcvt49.apps.googleusercontent.com";
    private final CredentialsManager mCredentialsManager;

    private final SingleFragmentActivity mActivity;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private ConnectionResult mConnectionResult;
    private boolean mSignInClicked;
    private boolean mSilent = false;


    public GoogleAuthenticationActivity() {
        mActivity = this;
        mCredentialsManager = CredentialsManager.getInstance(this);
    }

    private String getGoogleAccountName() {
        return getPreferences().getString("mGoogleAccountName", null);
    }

    private void setGoogleAccountName(String accountName) {
        getPreferences().edit().putString("mGoogleAccountName", accountName).commit();
    }

    public void startLogIn(boolean silent) {
        setStatusLabel(R.string.auth_progress_google_logging_in, 0);
        mSilent = silent;

        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            if (mConnectionResult == null) {
                mGoogleApiClient.connect();
            } else {
                resolveSignInError();
            }
        }
    }

    private void setStatusLabel(int resId, int detailsResId) {
        ((TextView) findViewById(R.id.authProgressHeader)).setText(getString(resId));
        if (detailsResId > 0) {
            ((TextView) findViewById(R.id.authProgressMessage)).setText(getString(detailsResId));
            ((TextView) findViewById(R.id.authProgressMessage)).setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.authProgressMessage)).setVisibility(View.GONE);
        }
    }

    public void startLogOut(boolean revokeAccess) {
        getPreferences().edit().remove("mGoogleAccountName").commit();
        if (revokeAccess) {
            setStatusLabel(R.string.auth_progress_logging_out, R.string.auth_progress_google_revokation);
            revokeAccess();
        } else {
            setStatusLabel(R.string.auth_progress_logging_out, 0);
            mCredentialsManager.onLogOutDone();
            finish();
        }
    }

    private void revokeAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (mGoogleApiClient.isConnected()) {
                        // Since the name "revokeAccessAndDisconnect"
                        // includes the word "Disconnect", one would
                        // think that explicitly invoking disconnect()
                        // would be redundant, but for some reason
                        // isConnected() seems to return true even after
                        // revokeAccessAndDisconnect has invoked the
                        // callback. So it seems like a good idea to
                        // explicitly call disconnect() just be air on
                        // the safe side.
                        mGoogleApiClient.disconnect();
                    }
                    // TODO: In the onResult callback, you can respond to the event and trigger any appropriate logic in your app or your back-end code. For more information, see the deletion rules in the developer policies. More on https://developers.google.com/+/policies#personal-information. We probably need to clear the user's e-mail address and name (which we got from their Google profile) from the server's database).
                    mCredentialsManager.onLogOutDone();
                    finish();
                }
            });
        } else {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
            mGoogleApiClient = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient = new GoogleApiClient.Builder(mActivity, this, this)
                .addApi(Plus.API)
//                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

        switch (getAction()) {
            case LOG_IN:
                startLogIn(false);
                break;
            case LOG_OUT:
                startLogOut(false);
                break;
            case LOG_OUT_AND_REVOKE:
                startLogOut(true);
        }
    }

    private Action getAction() {
        return Action.valueOf(getIntent().getStringExtra("mAction"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != FragmentActivity.RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;
            if (resultCode == FragmentActivity.RESULT_OK) {
                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
            } else if (resultCode == FragmentActivity.RESULT_CANCELED) {
                mCredentialsManager.onLogInCancelled();
                finish();
                Toast.makeText(mActivity, R.string.must_authorize_app, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == RC_CHOOSE_ACCOUNT) {
            if (resultCode == FragmentActivity.RESULT_OK) {
                final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                if (accountName != null) {
                    setGoogleAccountName(accountName);
                    requestGoogleIDToken(mActivity);
                } else {
                    startChooseAccountActivity();
                }
            } else if (resultCode == FragmentActivity.RESULT_CANCELED) {
                mCredentialsManager.onLogInCancelled();
                finish();
                Toast.makeText(mActivity, R.string.pick_account, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR) {
            requestGoogleIDToken(mActivity);
        }
    }

    /**
     * Google Play services will trigger the onConnectionSuspended callback if our Activity loses its service connection. Typically you will want to attempt to reconnect when this happens in order to retrieve a new ConnectionResult that can be resolved by the user.
     *
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), mActivity, 0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (!CredentialsManager.getInstance(mActivity).getState().isLoggedIn()) {
            mSignInClicked = false;
            if (getGoogleAccountName() == null) {
                startChooseAccountActivity();
            } else {
                requestGoogleIDToken(mActivity);
            }
        } else {
            if (getAction() == Action.LOG_OUT_AND_REVOKE) {
                revokeAccess();
            }
        }
    }

    private void requestGoogleIDToken(final SingleFragmentActivity activity) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    String scope = "audience:server:client_id:" + WEB_APP_CLIENT_ID;
                    String token = GoogleAuthUtil.getToken(activity, getGoogleAccountName(), scope);

                    /* mGoogleApiClient can be null if the activity ends before this AsyncTask has been run. */
                    boolean isClientSet = mGoogleApiClient != null;

                    if (isClientSet && token != null) {
                        Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                        UserPropertiesBean userInfo = null;
                        if (person != null) {
                            userInfo = new UserPropertiesBean();
                            userInfo.setDisplayName(person.getDisplayName());
                            userInfo.setName(getGoogleAccountName());
                        }

                        mCredentialsManager.onLogInDone(IdentityProvider.GOOGLE, new ServerAPICredentials(ServerAPICredentials.API_TOKEN_TYPE_GOOGLE, token), userInfo);
                        finish();
                    }
                } catch (final GoogleAuthException e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (e instanceof GooglePlayServicesAvailabilityException) {
                                GooglePlayServicesAvailabilityException exception = (GooglePlayServicesAvailabilityException) e;
                                // The Google Play services APK is old, disabled, or not present.
                                // Show a dialog created by Google Play services that allows
                                // the user to update the APK
                                int statusCode = exception.getConnectionStatusCode();
                                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                                        activity,
                                        REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                                dialog.show();
                            } else if (e instanceof UserRecoverableAuthException) {
                                UserRecoverableAuthException exception = (UserRecoverableAuthException) e;
                                // Unable to authenticate, such as when the user has not yet granted
                                // the app access to the account, but the user can fix this.
                                // Forward the user to an activity in Google Play services.
                                Intent intent = exception.getIntent();
                                activity.startActivityForResult(intent, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                            } else {
                                LogUtil.e(GoogleAuthenticationActivity.class.getName(), "Could not get Google ID token", e);
                                mCredentialsManager.onLogInCancelled();
                                Toast.makeText(mActivity, e.getClass().getSimpleName() + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                } catch (Throwable e) {
                    LogUtil.e(GoogleAuthenticationActivity.class.getName(), "Could not get Google ID token", e);
                    mCredentialsManager.onLogInCancelled();
                    finish();
                }
                return null;
            }
        }.execute();
    }

    private void startChooseAccountActivity() {
        if (!mSilent) {
            mActivity.startActivityForResult(AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"}, false, null, null, null, null), RC_CHOOSE_ACCOUNT);
        } else {
            mCredentialsManager.onLogInCancelled();
            finish();
            LogUtil.e(GoogleAuthenticationActivity.class.getName(), "Google account chooser should have been shown but will not be shown since silent=true.");
        }
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
//        if (!mSilent) {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(mActivity, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
//        } else {
//            LogUtil.e(GoogleAuthenticationActivity.class.getName(), "Will not handle sign-in error since silent=true.");
//        }
    }

    private SharedPreferences getPreferences() {
        return mActivity.getSharedPreferences(CredentialsManager.class.getName(), Context.MODE_PRIVATE);
    }
}
