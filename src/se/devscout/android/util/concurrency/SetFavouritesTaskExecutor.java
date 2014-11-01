package se.devscout.android.util.concurrency;

import android.content.Context;
import android.graphics.Bitmap;
import se.devscout.android.model.repo.remote.RemoteActivityRepoImpl;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.http.UnauthorizedException;
import se.devscout.android.util.http.UnhandledHttpResponseCodeException;

import java.io.IOException;

class SetFavouritesTaskExecutor implements BackgroundTasksHandlerThread.BackgroundTaskExecutor {
    @Override
    public Bitmap run(Object[] params, Context context) {
        try {
            RemoteActivityRepoImpl.getInstance(context).sendSetFavouritesRequest();
        } catch (IOException e) {
            LogUtil.e(BackgroundTasksHandlerThread.class.getName(), "Could not send favourites to server", e);
        } catch (UnauthorizedException e) {
            LogUtil.e(BackgroundTasksHandlerThread.class.getName(), "Could not send favourites to server", e);
        } catch (UnhandledHttpResponseCodeException e) {
            LogUtil.e(BackgroundTasksHandlerThread.class.getName(), "Could not send favourites to server", e);
        }
        return null;
    }
}
