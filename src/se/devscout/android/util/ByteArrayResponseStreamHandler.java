package se.devscout.android.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteArrayResponseStreamHandler implements ResponseStreamHandler<byte[]> {
    @Override
    public byte[] read(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int bytesRead = 0;
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((bytesRead = in.read(buffer)) > 0) {
            out.write(buffer, 0, bytesRead);
            length += bytesRead;
        }
        out.close();
        LogUtil.d(ByteArrayResponseStreamHandler.class.getName(), "Received " + length + " bytes from server.");
        return out.toByteArray();
    }
}
