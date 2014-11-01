package se.devscout.android.util.concurrency;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import se.devscout.android.R;
import se.devscout.android.util.*;
import se.devscout.android.util.http.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class DisplayImageTaskExecutor implements BackgroundTasksHandlerThread.BackgroundTaskExecutor {
    private final ResponseHeadersValidator MAXIMUM_SIZE_VALIDATOR = new ContentLengthValidator(20000);
    private List<URI> blockedURLs = Collections.synchronizedList(new ArrayList<URI>());
    public Context mContext;

    @Override
    public Bitmap run(Object[] params, Context context) {
        mContext = context;
        return handleDisplayImage(params);
    }

    private Bitmap handleDisplayImage(Object[] msg) {
        final URI uri;
        uri = (URI) msg[1];
        if (uri == null) {
            LogUtil.e(BackgroundTasksHandlerThread.class.getName(), "Nothing to do. No URI for object.");
            return null;
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
        return bitmap;
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
