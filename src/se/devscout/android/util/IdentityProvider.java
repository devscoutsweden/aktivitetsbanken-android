package se.devscout.android.util;

import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.android.util.auth.AuthenticationStrategy;
import se.devscout.android.util.auth.GoogleAuthenticationActivity;

public enum IdentityProvider implements AuthenticationStrategy {
    GOOGLE {
        @Override
        public void startLogIn(SingleFragmentActivity activity, boolean silent) {
            activity.startActivity(GoogleAuthenticationActivity.createLogInIntent(activity));
        }

        @Override
        public void startLogOut(SingleFragmentActivity activity, boolean revokeAccess) {
            activity.startActivity(GoogleAuthenticationActivity.createLogOutIntent(activity, revokeAccess));
        }
    }
}
