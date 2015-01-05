package se.devscout.android.util.concurrency;

import android.content.Context;
import se.devscout.android.R;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.auth.CredentialsManager;
import se.devscout.android.util.http.UnauthorizedException;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.UserKey;

public class UpdateFavouriteStatusTaskExecutor implements BackgroundTasksHandlerThread.BackgroundTaskExecutor<UnauthorizedException, UpdateFavouriteStatusParam> {
    @Override
    public UnauthorizedException run(UpdateFavouriteStatusParam param, Context context) {
        ActivityBank activityBank = ActivityBankFactory.getInstance(context);
        UserKey currentUser = CredentialsManager.getInstance(context).getCurrentUser();
        ActivityKey activityKey = param.getActivityKey();

        try {
            if (param.isToBeSetAsFavourite()) {
                activityBank.setFavourite(activityKey, currentUser);
            } else {
                activityBank.unsetFavourite(activityKey, currentUser);
            }
            return null;
        } catch (UnauthorizedException e) {
            LogUtil.e(UpdateFavouriteStatusTaskExecutor.class.getName(), "Could not send favourites to server", e);
            return new UnauthorizedException(context.getString(R.string.repo_unauthorized_could_not_save_favourites), e);
        }
    }
}
