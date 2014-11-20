package se.devscout.android.util.concurrency;

import android.content.Context;
import se.devscout.android.util.LogUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

public class CleanImageCacheTaskExecutor extends ImageCacheTaskExecutor {

    private static final int MINIMUM_TIME_BETWEEN_CLEANUP = 5 * 60 * 1000; // 5 minutes
    private static final Comparator<File> SORT_NEWEST_FIRST = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            return (int) (f2.lastModified() - f1.lastModified());
        }
    };
    private static long lastRun = 0;

    private static final int CACHE_SIZE_LIMIT = 500 * 1000; // 500 KB
    private int minimumTimeBetweenCleanup;
    private int cacheSizeLimit;

    public CleanImageCacheTaskExecutor() {
        this(CACHE_SIZE_LIMIT, MINIMUM_TIME_BETWEEN_CLEANUP);
    }

    public CleanImageCacheTaskExecutor(int cacheSizeLimit, int minimumTimeBetweenCleanup) {
        this.cacheSizeLimit = cacheSizeLimit;
        this.minimumTimeBetweenCleanup = minimumTimeBetweenCleanup;
    }

    @Override
    public Object run(Object[] params, Context context) {
        if (System.currentTimeMillis() - lastRun < minimumTimeBetweenCleanup) {
            LogUtil.d(CleanImageCacheTaskExecutor.class.getName(), "Will not purge image cache since it has been less than one minute since the last clean-up.");
            return null;
        } else {
            LogUtil.d(CleanImageCacheTaskExecutor.class.getName(), "Starting cache clean-up.");
            lastRun = System.currentTimeMillis();
        }
        File[] cachedFiles = getCachedFiles(context);
        long accumulatedSize = 0;
        for (File cachedFile : cachedFiles) {
            LogUtil.d(CleanImageCacheTaskExecutor.class.getName(), "Processing " + cachedFile.getName() + " ts=" + cachedFile.lastModified() + " size=" + cachedFile.length());
            if (accumulatedSize > cacheSizeLimit) {
                cachedFile.delete();
                LogUtil.d(CleanImageCacheTaskExecutor.class.getName(), "Deleted " + cachedFile.getName() + " since the total size of more recently modified files exceed " + cacheSizeLimit + " byte.");
            } else {
                accumulatedSize += cachedFile.length();
            }
        }
        return null;
    }

    private File[] getCachedFiles(Context context) {
        File[] cachedFiles = context.getCacheDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String file) {
                return file.startsWith(IMAGE_CACHE_FILENAME_PREFIX);
            }
        });
        Arrays.sort(cachedFiles, SORT_NEWEST_FIRST);
        return cachedFiles;
    }
}
