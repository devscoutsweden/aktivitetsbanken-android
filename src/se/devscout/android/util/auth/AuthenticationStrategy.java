package se.devscout.android.util.auth;

import se.devscout.android.controller.activity.SingleFragmentActivity;

/**
 * Implementations enable support for a specific user identity validator, e.g.
 * Google+ or Facebook. Implementations are expected to perform the validation
 * asynchronously, which may including starting/activating other installed apps
 * or launching the web browser. This is why the authentication strategy will
 * be notified of selected activity events (start, stop, result).
 */
public interface AuthenticationStrategy {

    void startLogIn(SingleFragmentActivity activity, boolean silent);

    void startLogOut(SingleFragmentActivity activity, boolean revokeAccess);

}
