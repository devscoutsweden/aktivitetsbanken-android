package se.devscout.android.util.http;

public class UnhandledHttpResponseCodeException extends Exception {
    private final int mResponseCode;

    public UnhandledHttpResponseCodeException(int responseCode) {
        mResponseCode = responseCode;
    }

    public int getResponseCode() {
        return mResponseCode;
    }
}
