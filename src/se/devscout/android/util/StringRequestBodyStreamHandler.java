package se.devscout.android.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class StringRequestBodyStreamHandler implements RequestBodyStreamHandler {
    private final String mBody;
    private Charset mCharset;

    public StringRequestBodyStreamHandler(String body, Charset charset) {
        mBody = body;
        mCharset = charset;
    }
    @Override
    public void write(OutputStream stream) throws IOException {
        stream.write(mBody.getBytes(mCharset));
    }
}
