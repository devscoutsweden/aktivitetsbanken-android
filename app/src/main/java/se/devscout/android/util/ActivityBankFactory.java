package se.devscout.android.util;

import android.content.Context;
import se.devscout.android.model.repo.ActivityBank;
import se.devscout.android.model.repo.remote.RemoteActivityRepoImpl;

public class ActivityBankFactory {
    private ActivityBankFactory() {
    }

    public static ActivityBank getInstance(Context context) {
        //TODO: use some kind of is-internet-connection-available feature to determine the type of object to return?
        return RemoteActivityRepoImpl.getInstance(context);
    }
}
