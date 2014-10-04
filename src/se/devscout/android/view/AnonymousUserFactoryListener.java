package se.devscout.android.view;

import java.util.EventListener;

public interface AnonymousUserFactoryListener extends EventListener {
    void onAnonymousUserCreated(boolean success);
}
