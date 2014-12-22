package se.devscout.android.util.concurrency;

public enum BackgroundTask {
    CLEAN_CACHE {
        @Override
        BackgroundTasksHandlerThread.BackgroundTaskExecutor createExecutor() {
            return new CleanImageCacheTaskExecutor();
        }
    },
    DISPLAY_IMAGE {
        @Override
        BackgroundTasksHandlerThread.BackgroundTaskExecutor createExecutor() {
            return new DisplayImageTaskExecutor();
        }
    },
    READ_ACTIVITY {
        @Override
        BackgroundTasksHandlerThread.BackgroundTaskExecutor createExecutor() {
            return new ReadActivitiesTaskExecutor();
        }
    };

    abstract BackgroundTasksHandlerThread.BackgroundTaskExecutor createExecutor();
}
