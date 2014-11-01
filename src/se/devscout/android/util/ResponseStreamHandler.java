package se.devscout.android.util;

import java.io.IOException;
import java.io.InputStream;

public interface ResponseStreamHandler<T> {
    T read(InputStream responseStream) throws IOException;
}
