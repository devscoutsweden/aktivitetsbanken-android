package se.devscout.android.util.concurrency;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;
import se.devscout.android.model.ActivityKey;
import se.devscout.android.util.LogUtil;

import java.net.URI;
import java.util.*;

public class BackgroundTasksHandlerThread extends HandlerThread {
    private int taskCount;
    private List<Listener> mListeners = new ArrayList<Listener>();
    private final List<ActivityKey> mPendingActivityReadRequests = new ArrayList<ActivityKey>();

    private final ReadActivitiesTaskParam mReadActivitiesTaskParam = new ReadActivitiesTaskParam() {
        @Override
        public ActivityKey[] getKeys() {
            // Return current list of activities which have not already been fetched from the API
            return getPendingActivityReadRequests();
        }
    };

    public static interface BackgroundTaskExecutor<R, P> {
        R run(P param, Context context);
    }


    private static final Map<BackgroundTask, BackgroundTaskExecutor> EXECUTORS = new HashMap<BackgroundTask, BackgroundTaskExecutor>();

    static {
        for (BackgroundTask backgroundTask : BackgroundTask.values()) {
            EXECUTORS.put(backgroundTask, backgroundTask.createExecutor());
        }
    }

    private Handler mHandler;
    private final Handler mResponseHandler;
    private final Context mContext;

    private final Map<Integer, BackgroundTask> mPendingTasks = new LinkedHashMap<Integer, BackgroundTask>();

    void postResponse(Runnable runnable) {
        mResponseHandler.post(runnable);
    }

    public interface Listener {
        ListenerAction onDone(Object parameter, Object response, BackgroundTask task);
    }

    public enum ListenerAction {
        KEEP,
        REMOVE
    }

    public BackgroundTasksHandlerThread(Context context, Handler responseHandler) {
        super(BackgroundTasksHandlerThread.class.getSimpleName());
        mResponseHandler = responseHandler;
        mContext = context;
    }

    @Override
    protected synchronized void onLooperPrepared() {
        mHandler = new MessageHandler();

        for (Map.Entry<Integer, BackgroundTask> entry : mPendingTasks.entrySet()) {
            Integer taskNumber = entry.getKey();
            BackgroundTask what = entry.getValue();
            LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Pending " + what.name() + " will now be scheduled for execution.");
            mHandler.obtainMessage(what.ordinal(), taskNumber).sendToTarget();
        }
    }

    private Map<Integer, Object> mTaskParameters = Collections.synchronizedMap(new HashMap<Integer, Object>());

    public synchronized void queueReadActivity(final ActivityKey activityKey) {
        addPendingActivityReadRequests(activityKey);

        queueTask(BackgroundTask.READ_ACTIVITY, mReadActivitiesTaskParam);
    }

    ActivityKey[] getPendingActivityReadRequests() {
        synchronized (mPendingActivityReadRequests) {
            ActivityKey[] keys = mPendingActivityReadRequests.toArray(new ActivityKey[mPendingActivityReadRequests.size()]);
            mPendingActivityReadRequests.clear();
            return keys;
        }
    }

    void addPendingActivityReadRequests(ActivityKey activityKey) {
        synchronized (mPendingActivityReadRequests) {
            mPendingActivityReadRequests.remove(activityKey);
            mPendingActivityReadRequests.add(0, activityKey);
        }
    }

    public void queueCleanCache() {
        queueTask(BackgroundTask.CLEAN_CACHE, null);
    }

    public void queueGetMediaResource(ImageView imageView, URI[] uris, int maxFileSize) {
        LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Wants to display one of " + TextUtils.join(", ", uris) + " in an image view.");
        queueTask(BackgroundTask.DISPLAY_IMAGE, new DisplayImageTaskParam(imageView, maxFileSize, uris));
    }

    public void queueUpdateFavouriteStatus(ActivityKey activityKey, boolean setToFavourite) {
        queueTask(BackgroundTask.UPDATE_FAVOURITE_STATUS, new UpdateFavouriteStatusParam(activityKey, setToFavourite));
    }

    private synchronized void queueTask(BackgroundTask task, Object parameter) {
        if (!isClosed()) {
            taskCount++;
            mTaskParameters.put(taskCount, parameter);
            if (mHandler == null) {
                LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Handler not yet initialised");
                mPendingTasks.put(taskCount, task);
            } else {
                mHandler.obtainMessage(task.ordinal(), taskCount).sendToTarget();
            }
        } else {
            LogUtil.i(BackgroundTasksHandlerThread.class.getName(), "Will not queue " + task.name() + " since the handler has been closed.");
        }
    }

    public synchronized void close() {
        LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Closing and clearing");
        mListeners.clear();
        mListeners = null;
        if (mHandler != null) {
            for (BackgroundTask task : BackgroundTask.values()) {
                mHandler.removeMessages(task.ordinal());
            }
        }
        mTaskParameters.clear();
        mTaskParameters = null;
    }

    public synchronized void addListener(Listener listener) {
        if (!isClosed() && !mListeners.contains(listener)) {
            mListeners.add(listener);
//            LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Added listener " + listener.toString());
        }
    }

    public synchronized void removeListener(Listener listener) {
        if (!isClosed() && mListeners.contains(listener)) {
            mListeners.remove(listener);
//            LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Removed listener " + listener.toString());
        }
    }

    private synchronized void fireOnDone(Object parameters, Object response, BackgroundTask task) {
//        LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Number of listeners: " + mListeners.size());
        if (!isClosed()) {

            int size = mListeners.size();
            for (int i = size - 1; i >= 0; i--) {
                Listener listener = mListeners.get(i);
                if (listener.onDone(parameters, response, task) == ListenerAction.REMOVE) {
                    mListeners.remove(i);
                }
            }
        }
    }

    private class MessageHandler extends Handler {

        @Override
        public void handleMessage(final Message msg) {
            final Object parameters;
            Object obj = msg.obj;
            final BackgroundTask task = BackgroundTask.values()[msg.what];
//            LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Background task " + obj + ": " + task.name() + " STARTED");
            synchronized (BackgroundTasksHandlerThread.this) {
                parameters = mTaskParameters.get(obj);
            }
//            LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Background task " + obj + ": " + task.name() + " BEFORE RUN");
            final Object result = EXECUTORS.get(task).run(parameters, mContext);
//            LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Background task " + obj + ": " + task.name() + " AFTER RUN");
            synchronized (BackgroundTasksHandlerThread.this) {
                if (!isClosed()) {
                    mTaskParameters.remove(obj);
                    postResponse(new Runnable() {
                        @Override
                        public void run() {
//                    LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Background task postResponse/fireOnDone");
                            fireOnDone(parameters, result, task);
                        }
                    });
                } else {
                    LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Background task completed after its associated handler thread was closed. Task's result will not be handled/processed.");
                }
            }
//            LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Background task " + obj + ": " + task.name() + " FINISHED");
        }

    }

    private synchronized boolean isClosed() {
        return mTaskParameters == null;
    }
}
