package se.devscout.android.util.http;

import android.text.TextUtils;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.StopWatch;
import se.devscout.android.util.UsageLogUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class HttpRequest {
    private static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    public static final String HEADER_CONTENT_ENCODING_GZIP = "gzip";
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 30000;
    public static final String HEADER_AUTHORIZATION = "Authorization";
    private static final int TIMEOUT_COUNT_FOR_HOST_BLOCK = 2;
    private final URL mUrl;
    private final HttpMethod mMethod;
    private final Map<String, String> mHeaders = new HashMap<String, String>();
    private static Map<String, Integer> mTimeouts = new HashMap<>();

    public HttpRequest(URL url, HttpMethod method) {
        mUrl = url;
        mMethod = method;
    }

    public <T> HttpResponse<T> run(ResponseStreamHandler<T> responseHandler, RequestBodyStreamHandler requestHandler) throws IOException, UnauthorizedException, UnhandledHttpResponseCodeException {
        try {
            return run(responseHandler, requestHandler, null);
        } catch (HeaderException e) {
            // Header exceptions will never actually be thrown since the responseHeadersValidator is null.
            return null;
        }
    }

    public <T> HttpResponse<T> run(ResponseStreamHandler<T> responseHandler, RequestBodyStreamHandler requestHandler, ResponseHeadersValidator responseHeadersValidator) throws IOException, UnauthorizedException, UnhandledHttpResponseCodeException, HeaderException {

//        assertHostAvailability(mUrl);

        HttpResponse<T> response = new HttpResponse<T>();

        StopWatch stopWatch = new StopWatch("readUrl " + mUrl);
        long startTime = System.currentTimeMillis();
        HttpURLConnection httpURLConnection = null;
        UsageLogUtil usageLogUtil = UsageLogUtil.getInstance();
        try {
            httpURLConnection = (HttpURLConnection) mUrl.openConnection();
            stopWatch.logEvent("Connection has been opened");
            httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
            httpURLConnection.setReadTimeout(READ_TIMEOUT);
            httpURLConnection.setRequestMethod(mMethod.name());

            for (Map.Entry<String, String> entry : mHeaders.entrySet()) {
                httpURLConnection.addRequestProperty(entry.getKey(), entry.getValue());
            }

            // Writing to output string will send request
            if (mMethod.isRequestBodyAllowed() && requestHandler != null) {
                httpURLConnection.setDoOutput(true);
                requestHandler.write(httpURLConnection.getOutputStream());
            }

            stopWatch.logEvent("Data sent");

            // Asking about response code will send request, if not already sent
            response.setCode(httpURLConnection.getResponseCode());
            if (responseHeadersValidator != null) {
                responseHeadersValidator.validate(httpURLConnection.getHeaderFields());
            }
            switch (httpURLConnection.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                case HttpURLConnection.HTTP_CREATED:
                    MeasuredBufferedInputStream measuredBufferedInputStream = new MeasuredBufferedInputStream(httpURLConnection.getInputStream());
                    InputStream is = measuredBufferedInputStream;
                    if (HEADER_CONTENT_ENCODING_GZIP.equals(httpURLConnection.getHeaderField(HEADER_CONTENT_ENCODING))) {
                        is = new GZIPInputStream(is);
                    }
                    response.setBody(responseHandler.read(is));
                    stopWatch.logEvent("Read " + measuredBufferedInputStream.getLength() + " bytes from connection.");
                    usageLogUtil.logHttpRequest(measuredBufferedInputStream.getLength());
                    is.close();
                    break;
                case HttpURLConnection.HTTP_NO_CONTENT:
                    break;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                case HttpURLConnection.HTTP_FORBIDDEN:
                    throw new UnauthorizedException(!TextUtils.isEmpty(httpURLConnection.getRequestProperty(HEADER_AUTHORIZATION)));
                default:
                    throw new UnhandledHttpResponseCodeException(httpURLConnection.getResponseCode());
            }
        } catch (SocketTimeoutException e) {
            logTimeout(mUrl);
            long now = System.currentTimeMillis();
            String timeString = String.valueOf(now - startTime);
            stopWatch.logEvent("Http request failed because of socket timeout after roughly " + timeString + " ms.");
            usageLogUtil.logHttpTimeout();
            LogUtil.i(HttpRequest.class.getName(), "Http request #" + (usageLogUtil.getHttpRequestCount() + 1) + " failed because of timeout #" + usageLogUtil.getHttpTimeouts() + ". Wait: " + timeString + " ms. URL: " + mUrl, e);
            throw e;
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            stopWatch.logEvent("Done");
            LogUtil.d(HttpRequest.class.getName(), stopWatch.getSummary());
        }
        return response;
    }

    private void logTimeout(URL url) {
        synchronized (HttpRequest.class) {
            if (mTimeouts.containsKey(url.getHost())) {
                mTimeouts.put(url.getHost(), mTimeouts.get(url.getHost()) + 1);
            } else {
                mTimeouts.put(url.getHost(), 1);
            }
        }
    }

    private void assertHostAvailability(URL url) throws SocketTimeoutException {
        synchronized (HttpRequest.class) {
            Integer count = mTimeouts.get(url.getHost());
            if (count != null && count > TIMEOUT_COUNT_FOR_HOST_BLOCK) {
                String message = "Will not attempt to connect to " + url.getHost() + " due to earlier failures.";
                LogUtil.i(HttpRequest.class.getName(), message);
                throw new SocketTimeoutException(message);
            }
        }
    }

    public void setHeader(String header, String value) {
        if (value != null) {
            mHeaders.put(header, value);
        } else {
            mHeaders.remove(header);
        }
    }

    private static class MeasuredBufferedInputStream extends BufferedInputStream {
        private int mLength;

        public MeasuredBufferedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public synchronized int read(byte[] buffer, int offset, int byteCount) throws IOException {
            mLength += byteCount;
            return super.read(buffer, offset, byteCount);
        }

        @Override
        public synchronized int read() throws IOException {
            mLength++;
            return super.read();
        }

        private int getLength() {
            return mLength;
        }
    }
}
