package se.devscout.android.util.concurrency;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import se.devscout.android.R;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.http.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class DisplayImageTaskExecutor extends ImageCacheTaskExecutor {
    private Map<URI, Exception> blockedURLs = Collections.synchronizedMap(new HashMap<URI, Exception>());

    @Override
    public Object run(Object[] params, Context context) {
        final URI[] uris = (URI[]) params[1];
        int maxFileSize = params.length > 2 && params[2] != null && params[2] instanceof Integer ? (Integer) params[2] : 10000;
        if (uris == null || uris.length == 0) {
            LogUtil.e(BackgroundTasksHandlerThread.class.getName(), "Nothing to do. No URI for object.");
            return null;
        } else {
            Exception lastException = null;
            for (URI uri : uris) {
                if (uri != null) {
                    File cacheFile = getCacheFile(uri, context);
                    if (blockedURLs.containsKey(uri)) {
                        LogUtil.i(BackgroundTasksHandlerThread.class.getName(), "Because of earlier failure, downloading " + uri + " will not be reattempted. The earlier failure was caused by a " + blockedURLs.get(uri).getClass().getName() + ".");
                        lastException = blockedURLs.get(uri);
                    } else if (cacheFile.exists()) {
                        LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Retrieved " + uri + " from cache.");
                        return getBitmapFromFile(cacheFile);
                    } else {
                        try {
                            Bitmap bitmap = getBitmapFromURI(uri, true, context, maxFileSize);
                            LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Downloaded image from " + uri);
                            return bitmap;
                        } catch (Exception e) {
                            blockedURLs.put(uri, e);
                            LogUtil.i(BackgroundTasksHandlerThread.class.getName(), "Could not (or would not) download " + uri, e);
                            lastException = e;
                        }
                    }
                } else {
                    LogUtil.e(BackgroundTasksHandlerThread.class.getName(), "Nothing to do. No URI specified.");
                }
            }
            return lastException;
        }
    }

    private Bitmap getBitmapFromURI(URI uri, boolean storeInCache, Context context, int maxFileSize) throws IOException, UnauthorizedException, UnhandledHttpResponseCodeException, HeaderException {
        Bitmap bitmap;
        URL url = uri.toURL();
        HttpRequest request = new HttpRequest(url, HttpMethod.GET);

        ResponseStreamHandler<byte[]> responseHandler = new ByteArrayResponseStreamHandler();
        HttpResponse<byte[]> response = request.run(responseHandler, null, new ContentLengthValidator(maxFileSize));

        byte[] data = response.getBody();
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        if (storeInCache) {
            storeFile(getCacheFile(uri, context), data);
        }
        return bitmap;
    }

    private Bitmap getBitmapFromResource(Context context) {
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_picture);
        return bitmap;
    }

}
