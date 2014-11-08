package se.devscout.server.api;

public interface OnReadDoneCallback<T> {
    void onRead(T object);
}
