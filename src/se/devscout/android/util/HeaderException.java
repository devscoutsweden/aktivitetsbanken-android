package se.devscout.android.util;

public class HeaderException extends Throwable {
    public HeaderException() {
    }

    public HeaderException(String detailMessage) {
        super(detailMessage);
    }

    public HeaderException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public HeaderException(Throwable throwable) {
        super(throwable);
    }
}
