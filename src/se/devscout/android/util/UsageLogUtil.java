package se.devscout.android.util;

public class UsageLogUtil {
    private static UsageLogUtil instance;

    private long mHttpBytesRead = 0;
    private int mHttpRequestCount = 0;

    private UsageLogUtil() {
    }

    public static UsageLogUtil getInstance() {
        if (instance == null) {
            synchronized (UsageLogUtil.class) {
                if (instance == null) {
                    instance = new UsageLogUtil();
                }
            }
        }
        return instance;
    }

    public void logHttpRequest(long bytesRead) {
        mHttpBytesRead += bytesRead;
        mHttpRequestCount++;
    }

    public long getHttpBytesRead() {
        return mHttpBytesRead;
    }

    public int getHttpRequestCount() {
        return mHttpRequestCount;
    }
}
