package se.devscout.android.util;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.ImageView;

import java.net.URI;
import java.util.*;

public class BackgroundTasksHandlerThread extends HandlerThread {
    private int taskCount;
    private List<Listener> mListeners = new ArrayList<Listener>();

    static interface BackgroundTaskExecutor {
        Object run(Object[] params, Context context);
    }

    public static enum BackgroundTask {
        SET_FAVOURIT,
        DISPLAY_IMAGE;
    }

    private Handler mHandler;
    private Handler mResponseHandler;
    private Context mContext;

    private List<Integer> mPendingGetMediaResources = new ArrayList();
    public void postReponse(Runnable runnable) {
        mResponseHandler.post(runnable);
    }

    public interface Listener {
        void onDone(Object[] token, Object response);
    }

    public BackgroundTasksHandlerThread(Context context, Handler responseHandler) {
        super(BackgroundTasksHandlerThread.class.getSimpleName());
        mResponseHandler = responseHandler;
        mContext = context;
    }

    @Override
    protected synchronized void onLooperPrepared() {
        final Map<BackgroundTask, BackgroundTaskExecutor> executors = new HashMap<BackgroundTask, BackgroundTaskExecutor>();
        executors.put(BackgroundTask.SET_FAVOURIT, new SetFavouritesTaskExecutor());
        executors.put(BackgroundTask.DISPLAY_IMAGE, new DisplayImageTaskExecutor());
        mHandler = new Handler() {

            @Override
            public void handleMessage(final Message msg) {
                final Object[] parameters = mTaskParameters.get(msg.obj);
                final Object result = executors.get(BackgroundTask.values()[msg.what]).run(parameters, mContext);
                mTaskParameters.remove(msg.obj);
                postReponse(new Runnable() {
                    @Override
                    public void run() {
                        fireOnDone(parameters, result);
                    }
                });
            }

        };
        for (Integer pendingGetMediaResource : mPendingGetMediaResources) {
            LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Sending pending message to target");
            mHandler.obtainMessage(BackgroundTask.DISPLAY_IMAGE.ordinal(), pendingGetMediaResource).sendToTarget();
        }
    }

    private Map<Integer, Object[]> mTaskParameters = Collections.synchronizedMap(new HashMap<Integer, Object[]>());

    public void queueSetFavourites() {
        mHandler.obtainMessage(BackgroundTask.SET_FAVOURIT.ordinal()).sendToTarget();
    }

    public synchronized void queueGetMediaResource(ImageView imageView, URI uri) {
        taskCount++;
        Object[] parameters = {imageView, uri};
        mTaskParameters.put(taskCount, parameters);
        if (mHandler == null) {
            LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Handler not yet initialised");
            mPendingGetMediaResources.add(taskCount);
        } else {
            LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Wants to display " + uri + " in an image view.");
            mHandler.obtainMessage(BackgroundTask.DISPLAY_IMAGE.ordinal(), taskCount).sendToTarget();
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

    private void fireOnDone(Object[] parameters, Object response) {
        for (Listener listener : mListeners) {
            listener.onDone(parameters, response);
        }
    }

}
