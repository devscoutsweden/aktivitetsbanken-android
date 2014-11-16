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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class DisplayImageTaskExecutor extends ImageCacheTaskExecutor {
    private List<URI> blockedURLs = Collections.synchronizedList(new ArrayList<URI>());

    @Override
    public Bitmap run(Object[] params, Context context) {
        final URI uri = (URI) params[1];
        int maxFileSize = params.length > 2 && params[2] != null && params[2] instanceof Integer ? (Integer) params[2] : 10000;
        if (uri == null) {
            LogUtil.e(BackgroundTasksHandlerThread.class.getName(), "Nothing to do. No URI for object.");
            return null;
        }
        File cacheFile = getCacheFile(uri, context);
        Bitmap bitmap;
        if (blockedURLs.contains(uri)) {
            bitmap = getBitmapFromResource(context);
            LogUtil.i(BackgroundTasksHandlerThread.class.getName(), "Because of earlier failure, downloading " + uri + " will not be reattempted.");
        } else if (cacheFile.exists()) {
            bitmap = getBitmapFromFile(cacheFile);
            LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Retrieved " + uri + " from cache.");
        } else {
            try {
                bitmap = getBitmapFromURI(uri, true, context, maxFileSize);
                LogUtil.d(BackgroundTasksHandlerThread.class.getName(), "Downloaded image from " + uri);
            } catch (HeaderException e) {
                blockedURLs.add(uri);
                bitmap = getBitmapFromResource(context);
                LogUtil.i(BackgroundTasksHandlerThread.class.getName(), "Could not (or would not) download " + uri, e);
            } catch (IOException e) {
                blockedURLs.add(uri);
                bitmap = getBitmapFromResource(context);
                LogUtil.e(BackgroundTasksHandlerThread.class.getName(), "Could not download " + uri, e);
            } catch (UnauthorizedException e) {
                blockedURLs.add(uri);
                bitmap = getBitmapFromResource(context);
                LogUtil.e(BackgroundTasksHandlerThread.class.getName(), "Could not download " + uri, e);
            } catch (UnhandledHttpResponseCodeException e) {
                blockedURLs.add(uri);
                bitmap = getBitmapFromResource(context);
                LogUtil.e(BackgroundTasksHandlerThread.class.getName(), "Could not download " + uri, e);
            }
        }
        return bitmap;
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
