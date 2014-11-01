package se.devscout.android.model.repo.remote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.ImageView;
import se.devscout.android.R;
import se.devscout.android.util.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class BackgroundTasksHandlerThread extends HandlerThread {
    private static interface BackgroundTaskExecutor {
        void run(Message message, Context context);
    }

    public static enum BackgroundTask {
        SET_FAVOURIT,
        DISPLAY_IMAGE;
    }

    private Handler mHandler;
    private Handler mResponseHandler;
    private Context mContext;
    private Listener mListener;

    private List mPendingGetMediaResources = new ArrayList();
    public void postReponse(Runnable runnable) {
        mResponseHandler.post(runnable);
    }

    public interface Listener {
        void onDone(Object token, Object response);
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
                executors.get(BackgroundTask.values()[msg.what]).run(msg, mContext);
            }

        };
        for (Object pendingGetMediaResource : mPendingGetMediaResources) {
            LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Sending pending message to target");
            mHandler.obtainMessage(BackgroundTask.DISPLAY_IMAGE.ordinal(), pendingGetMediaResource).sendToTarget();
        }
    }

    private Map<Object, URI> mMediaResourceRequests = Collections.synchronizedMap(new HashMap<Object, URI>());

    private URI getRequestedMediaResource(Object obj) {
        return mMediaResourceRequests.get(obj);
    }

    private void removeRequestedMediaResource(Object obj) {
        mMediaResourceRequests.remove(obj);
    }

    public void queueSetFavourites() {
        mHandler.obtainMessage(BackgroundTask.SET_FAVOURIT.ordinal()).sendToTarget();
    }

    public synchronized void queueGetMediaResource(ImageView imageView, URI uri) {
        mMediaResourceRequests.put(imageView, uri);
        if (mHandler == null) {
            LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Handler not yet initialised");
            mPendingGetMediaResources.add(imageView);
        } else {
            LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Wants to display " + uri + " in an image view.");
            mHandler.obtainMessage(BackgroundTask.DISPLAY_IMAGE.ordinal(), imageView).sendToTarget();
        }
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    private void fireOnDone(Object token, Object response) {
        mListener.onDone(token, response);
    }

    private static class SetFavouritesTaskExecutor implements BackgroundTaskExecutor {
        @Override
        public void run(Message message, Context context) {
            try {
                RemoteActivityRepoImpl.getInstance(context).sendSetFavouritesRequest();
            } catch (IOException e) {
                LogUtil.e(BackgroundTasksHandlerThread.class.getName(), "Could not send favourites to server", e);
            } catch (UnauthorizedException e) {
                LogUtil.e(BackgroundTasksHandlerThread.class.getName(), "Could not send favourites to server", e);
            } catch (UnhandledHttpResponseCodeException e) {
                LogUtil.e(BackgroundTasksHandlerThread.class.getName(), "Could not send favourites to server", e);
            }
        }
    }

    private class DisplayImageTaskExecutor implements BackgroundTaskExecutor {
        private final ResponseHeadersValidator MAXIMUM_SIZE_VALIDATOR = new ContentLengthValidator(20000);
        private List<URI> blockedURLs = Collections.synchronizedList(new ArrayList<URI>());
        public Context mContext;

        @Override
        public void run(Message message, Context context) {
            mContext = context;
            handleDisplayImage(message);
        }

        private void handleDisplayImage(Message msg) {
            final Object obj;
            final URI uri;
            obj = msg.obj;
            uri = getRequestedMediaResource(obj);
            if (uri == null) {
                LogUtil.e(BackgroundTasksHandlerThread.class.getName(), "Nothing to do. No URI for object.");
                return;
            }
            File cacheFile = getCacheFile(uri);
            Bitmap bitmap;
            if (blockedURLs.contains(uri)) {
                bitmap = getBitmapFromResource();
                LogUtil.i(BackgroundTasksHandlerThread.class.getName(), "Because of earlier failure, downloading " + uri + " will not be reattempted.");
            } else if (cacheFile.exists()) {
                bitmap = getBitmapFromFile(cacheFile);
                LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Retrieved " + uri + " from cache.");
            } else {
                try {
                    bitmap = getBitmapFromURI(uri, true);
                    LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Downloaded image from " + uri);
                } catch (HeaderException e) {
                    blockedURLs.add(uri);
                    bitmap = getBitmapFromResource();
                    LogUtil.i(BackgroundTasksHandlerThread.class.getName(), "Could not (or would not) download " + uri, e);
                } catch (IOException e) {
                    blockedURLs.add(uri);
                    bitmap = getBitmapFromResource();
                    LogUtil.e(BackgroundTasksHandlerThread.class.getName(), "Could not download " + uri, e);
                } catch (UnauthorizedException e) {
                    blockedURLs.add(uri);
                    bitmap = getBitmapFromResource();
                    LogUtil.e(BackgroundTasksHandlerThread.class.getName(), "Could not download " + uri, e);
                } catch (UnhandledHttpResponseCodeException e) {
                    blockedURLs.add(uri);
                    bitmap = getBitmapFromResource();
                    LogUtil.e(BackgroundTasksHandlerThread.class.getName(), "Could not download " + uri, e);
                }
            }
            postResponse(obj, uri, bitmap);
        }

        private void postResponse(final Object obj, final URI uri, final Bitmap bitmap) {
            postReponse(new Runnable() {
                @Override
                public void run() {
                    if (getRequestedMediaResource(obj) != uri) {
                        return;
                    }

                    removeRequestedMediaResource(obj);
                    fireOnDone(obj, bitmap);
                }
            });
        }

        private Bitmap getBitmapFromURI(URI uri, boolean storeInCache) throws IOException, UnauthorizedException, UnhandledHttpResponseCodeException, HeaderException {
            Bitmap bitmap;
            URL url = uri.toURL();
            HttpRequest request = new HttpRequest(url, HttpMethod.GET);

            ResponseStreamHandler<byte[]> responseHandler = new ByteArrayResponseStreamHandler();
            HttpResponse<byte[]> response = request.run(responseHandler, null, MAXIMUM_SIZE_VALIDATOR);

            byte[] data = response.getBody();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            if (storeInCache) {
                storeFile(getCacheFile(uri), data);
            }
            return bitmap;
        }

        private File getCacheFile(URI uri) {
            return new File(mContext.getCacheDir(), "image-cache-" + md5(uri.toString()));
        }

        private Bitmap getBitmapFromResource() {
            Bitmap bitmap;
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_action_picture);
            return bitmap;
        }

        private Bitmap getBitmapFromFile(File file) {
            Bitmap bitmap;
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            file.setLastModified(System.currentTimeMillis());
            return bitmap;
        }

        private void storeFile(File cacheFile, byte[] data) throws IOException {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(cacheFile));
            bos.write(data);
            bos.close();
            LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Saved data as " + cacheFile.getAbsolutePath());
        }

        /**
         * http://stackoverflow.com/a/4846511
         */
        public final String md5(final String s) {
            final String MD5 = "MD5";
            try {
                // Create MD5 Hash
                MessageDigest digest = MessageDigest.getInstance(MD5);
                digest.update(s.getBytes());
                byte messageDigest[] = digest.digest();

                // Create Hex String
                StringBuilder hexString = new StringBuilder();
                for (byte aMessageDigest : messageDigest) {
                    String h = Integer.toHexString(0xFF & aMessageDigest);
                    while (h.length() < 2) {
                        h = "0" + h;
                    }
                    hexString.append(h);
                }
                return hexString.toString();

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return "";
        }
    }
}
