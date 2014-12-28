package se.devscout.android.util;

import android.content.Intent;
import se.devscout.android.controller.activity.SingleFragmentActivity;

public interface AuthenticationStrategy {

    void startLogIn();

    void startLogOut();

    void onActivityStop(SingleFragmentActivity activity);

    void onActivityStart(SingleFragmentActivity activity);

    void onActivityResult(int requestCode, int resultCode, Intent data, SingleFragmentActivity activity);

//    interface Callback {
//        void onLogInDone(IdentityProvider provider, String data, UserProperties userProperties);
//        void onLogOutDone();
//    }
}
