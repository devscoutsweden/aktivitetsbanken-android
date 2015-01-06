package se.devscout.android.util.http;

import se.devscout.android.util.LogUtil;
import se.devscout.android.util.StopWatch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteArrayResponseStreamHandler implements ResponseStreamHandler<byte[]> {
    @Override
    public byte[] read(InputStream in) throws IOException {
        StopWatch stopWatch = new StopWatch("Reading stream");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int bytesRead;
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((bytesRead = in.read(buffer)) > 0) {
            out.write(buffer, 0, bytesRead);
            length += bytesRead;
        }
        out.close();
        stopWatch.logEvent("Received " + length + " bytes from server.");
        LogUtil.d(ByteArrayResponseStreamHandler.class.getName(), stopWatch.getSummary());
        return out.toByteArray();
    }
}
