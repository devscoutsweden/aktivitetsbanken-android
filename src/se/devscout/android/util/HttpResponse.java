package se.devscout.android.util;

public class HttpResponse<T> {

    private T mBody;
    private int mCode;

    public T getBody() {
        return mBody;
    }

    public void setBody(T body) {
        mBody = body;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public int getCode() {
        return mCode;
    }
}
