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
    },
    UPDATE_FAVOURITE_STATUS {
        @Override
        BackgroundTasksHandlerThread.BackgroundTaskExecutor createExecutor() {
            return new UpdateFavouriteStatusTaskExecutor();
        }
    };

    abstract BackgroundTasksHandlerThread.BackgroundTaskExecutor createExecutor();
}
