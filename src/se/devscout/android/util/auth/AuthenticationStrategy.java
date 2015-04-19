package se.devscout.android.util.auth;

import android.content.Intent;

/**
 * Implementations enable support for a specific user identity validator, e.g.
 * Google+ or Facebook. Implementations are expected to perform the validation
 * asynchronously, which may including starting/activating other installed apps
 * or launching the web browser. This is why the authentication strategy will
 * be notified of selected activity events (start, stop, result).
 */
public interface AuthenticationStrategy {

    void startLogIn(boolean silent);

    void startLogOut(boolean revokeAccess);

    void onActivityStop();

    void onActivityStart();

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
