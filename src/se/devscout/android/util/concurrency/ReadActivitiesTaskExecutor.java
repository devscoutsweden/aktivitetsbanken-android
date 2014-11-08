package se.devscout.android.util.concurrency;

import android.content.Context;
import se.devscout.android.model.repo.remote.RemoteActivityRepoImpl;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.http.UnauthorizedException;
import se.devscout.server.api.model.ActivityKey;

public class ReadActivitiesTaskExecutor implements BackgroundTasksHandlerThread.BackgroundTaskExecutor {
    @Override
    public Object run(Object[] params, Context context) {
        RemoteActivityRepoImpl remoteActivityRepo = (RemoteActivityRepoImpl) params[0];

        try {
            ActivityKey[] keys = remoteActivityRepo.getPendingActivityReadRequests();
            return remoteActivityRepo.readActivities(keys);
        } catch (UnauthorizedException e) {
            LogUtil.e(ReadActivitiesTaskExecutor.class.getName(), "Could not get activity data from server", e);
            return null;
        }
    }
}
