package se.devscout.android.util.http;

import se.devscout.android.util.LogUtil;
import se.devscout.android.util.StopWatch;

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
        StopWatch stopWatch = new StopWatch("Writing to stream");
        byte[] bytes = mBody.getBytes(mCharset);
        stream.write(bytes);
        stopWatch.logEvent("Sent " + bytes.length + " bytes to server.");
        LogUtil.d(StringRequestBodyStreamHandler.class.getName(), stopWatch.getSummary());
    }
}
