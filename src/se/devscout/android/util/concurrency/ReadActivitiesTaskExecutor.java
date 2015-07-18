package se.devscout.android.util.concurrency;

import android.content.Context;
import se.devscout.android.model.Activity;
import se.devscout.android.model.ActivityKey;
import se.devscout.android.model.ActivityList;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.http.UnauthorizedException;

import java.util.List;

public class ReadActivitiesTaskExecutor implements BackgroundTasksHandlerThread.BackgroundTaskExecutor<List<? extends Activity>,ReadActivitiesTaskParam> {
    @Override
    public ActivityList run(ReadActivitiesTaskParam param, Context context) {
        try {
            ActivityKey[] keys = param.getKeys();
            return ActivityBankFactory.getInstance(context).readActivities(keys);
        } catch (UnauthorizedException e) {
            LogUtil.e(ReadActivitiesTaskExecutor.class.getName(), "Could not get activity data from server", e);
            return null;
        }
    }
}
