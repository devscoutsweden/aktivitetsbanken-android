package se.devscout.android.util.concurrency;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.ImageView;
import se.devscout.android.util.LogUtil;

import java.net.URI;
import java.util.*;

public class BackgroundTasksHandlerThread extends HandlerThread {
    private int taskCount;
    private List<Listener> mListeners = new ArrayList<Listener>();

    static interface BackgroundTaskExecutor {
        Object run(Object[] params, Context context);
    }

    private Handler mHandler;
    private Handler mResponseHandler;
    private Context mContext;

    private Map<Integer, BackgroundTask> mPendingTasks = new LinkedHashMap<Integer, BackgroundTask>();
    public void postReponse(Runnable runnable) {
        mResponseHandler.post(runnable);
    }

    public interface Listener {
        void onDone(Object[] token, Object response, BackgroundTask task);
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

    private Map<Integer, Object[]> mTaskParameters = Collections.synchronizedMap(new HashMap<Integer, Object[]>());

    public void queueSetFavourites() {
        queueTask(BackgroundTask.SET_FAVOURITES);
    }

    public void queueCleanCache() {
        queueTask(BackgroundTask.CLEAN_CACHE);
    }

    public synchronized void queueGetMediaResource(ImageView imageView, URI uri) {
        LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Wants to display " + uri + " in an image view.");
        queueTask(BackgroundTask.DISPLAY_IMAGE, imageView, uri);
    }

    private void queueTask(BackgroundTask task, Object... parameters) {
        taskCount++;
        mTaskParameters.put(taskCount, parameters);
        if (mHandler == null) {
            LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Handler not yet initialised");
            mPendingTasks.put(taskCount, task);
        } else {
            mHandler.obtainMessage(task.ordinal(), taskCount).sendToTarget();
        }
    }

    public void close() {
        mListeners.clear();
        for (BackgroundTask task : BackgroundTask.values()) {
            mHandler.removeMessages(task.ordinal());
        }
        mTaskParameters.clear();
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    private void fireOnDone(Object[] parameters, Object response, BackgroundTask task) {
        for (Listener listener : mListeners) {
            listener.onDone(parameters, response, task);
        }
    }

    private class MessageHandler extends Handler {

        private Map<BackgroundTask,BackgroundTaskExecutor> mExecutors = new HashMap<BackgroundTask, BackgroundTaskExecutor>();

        {
            for (BackgroundTask backgroundTask : BackgroundTask.values()) {
                mExecutors.put(backgroundTask, backgroundTask.createExecutor());
            }
        }

        @Override
        public void handleMessage(final Message msg) {
            final Object[] parameters = mTaskParameters.get(msg.obj);
            final BackgroundTask task = BackgroundTask.values()[msg.what];
            LogUtil.i(BackgroundTasksHandlerThread.class.getName(), "Executing background task " + task.name());
            final Object result = mExecutors.get(task).run(parameters, mContext);
            mTaskParameters.remove(msg.obj);
            postReponse(new Runnable() {
                @Override
                public void run() {
                    fireOnDone(parameters, result, task);
                }
            });
        }

    }
}
