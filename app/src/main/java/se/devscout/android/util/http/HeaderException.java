package se.devscout.android.util.http;

public class HeaderException extends Exception {
    public HeaderException() {
    }

    public HeaderException(String detailMessage) {
        super(detailMessage);
    }

    HeaderException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    HeaderException(Throwable throwable) {
        super(throwable);
    }
}
