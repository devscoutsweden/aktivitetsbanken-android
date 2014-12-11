package se.devscout.android.util.concurrency;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import se.devscout.android.util.LogUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class ImageCacheTaskExecutor<R, P> implements BackgroundTasksHandlerThread.BackgroundTaskExecutor<R, P> {
    protected static final String IMAGE_CACHE_FILENAME_PREFIX = "image-cache-";

    protected File getCacheFile(URI uri, Context context) {
        return new File(context.getCacheDir(), IMAGE_CACHE_FILENAME_PREFIX + md5(uri.toString()));
    }

    protected Bitmap getBitmapFromFile(File file) {
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        file.setLastModified(System.currentTimeMillis());
        return bitmap;
    }

    protected void storeFile(File cacheFile, byte[] data) throws IOException {
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
