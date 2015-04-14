package se.devscout.android.util.http;

public class UnauthorizedException extends Exception {
    private boolean authorizationHeaderProvided;

    public UnauthorizedException() {
    }

    public UnauthorizedException(String detailMessage) {
        super(detailMessage);
    }

    public UnauthorizedException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public UnauthorizedException(Throwable throwable) {
        super(throwable);
    }

    public UnauthorizedException(boolean authorizationHeaderProvided) {
        this.authorizationHeaderProvided = authorizationHeaderProvided;
    }

    public boolean isAuthorizationHeaderProvided() {
        return authorizationHeaderProvided;
    }
}
