package se.devscout.server.api.model;

public interface Range<T extends Number> {
    T getMin();

    T getMax();
}
