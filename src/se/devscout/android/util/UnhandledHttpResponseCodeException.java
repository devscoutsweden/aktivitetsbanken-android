package se.devscout.android.util;

public class UnhandledHttpResponseCodeException extends Throwable {
    private int mResponseCode;

    public UnhandledHttpResponseCodeException(int responseCode) {
        mResponseCode = responseCode;
    }

    public int getResponseCode() {
        return mResponseCode;
    }
}
