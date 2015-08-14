package se.devscout.android.util.http;

public enum HttpMethod {
    POST(true),
    PUT(true),
    GET(false),
    DELETE(false),
    PATCH(true);

    private boolean mRequestBodyAllowed;

    private HttpMethod(boolean requestBodyAllowed) {
        mRequestBodyAllowed = requestBodyAllowed;
    }

    public boolean isRequestBodyAllowed() {
        return mRequestBodyAllowed;
    }
}
