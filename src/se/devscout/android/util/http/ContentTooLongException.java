package se.devscout.android.util.http;

public class ContentTooLongException extends HeaderException {
    private long length;
    private long maxLength;

    public ContentTooLongException(long length, long maxLength) {
        this.length = length;
        this.maxLength = maxLength;
    }

    public ContentTooLongException(String detailMessage, long length, long maxLength) {
        super(detailMessage);
        this.length = length;
        this.maxLength = maxLength;
    }

    public ContentTooLongException(String detailMessage, Throwable throwable, long length, long maxLength) {
        super(detailMessage, throwable);
        this.length = length;
        this.maxLength = maxLength;
    }

    public ContentTooLongException(Throwable throwable, long length, long maxLength) {
        super(throwable);
        this.length = length;
        this.maxLength = maxLength;
    }

    public long getLength() {
        return length;
    }

    public long getMaxLength() {
        return maxLength;
    }
}
