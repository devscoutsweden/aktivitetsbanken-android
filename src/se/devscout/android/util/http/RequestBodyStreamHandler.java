package se.devscout.android.util.http;

import java.io.IOException;
import java.io.OutputStream;

public interface RequestBodyStreamHandler {
    void write(OutputStream stream) throws IOException;
}
