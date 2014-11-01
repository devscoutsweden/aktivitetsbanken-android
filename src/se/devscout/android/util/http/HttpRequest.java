package se.devscout.android.util.http;

import se.devscout.android.util.LogUtil;
import se.devscout.android.util.StopWatch;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final URL mUrl;
    private final HttpMethod mMethod;
    private Map<String, String> mHeaders = new HashMap<String, String>();

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
        HttpResponse<T> response = new HttpResponse<T>();

        StopWatch stopWatch = new StopWatch("readUrl " + mUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) mUrl.openConnection();
        stopWatch.logEvent("Opened connection");
        try {
            httpURLConnection.setRequestMethod(mMethod.name());

            for (Map.Entry<String, String> entry : mHeaders.entrySet()) {
                httpURLConnection.addRequestProperty(entry.getKey(), entry.getValue());
            }

            // Writing to output string will send request
            if (mMethod != HttpMethod.GET && requestHandler != null) {
                requestHandler.write(httpURLConnection.getOutputStream());
            }

            // Asking about response code will send request, if not already sent
            response.setCode(httpURLConnection.getResponseCode());
            if (responseHeadersValidator != null) {
                responseHeadersValidator.validate(httpURLConnection.getHeaderFields());
            }
            switch (httpURLConnection.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                case HttpURLConnection.HTTP_CREATED:
                    response.setBody(responseHandler.read(httpURLConnection.getInputStream()));
                    break;
                case HttpURLConnection.HTTP_NO_CONTENT:
                    break;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    throw new UnauthorizedException();
                default:
                    throw new UnhandledHttpResponseCodeException(httpURLConnection.getResponseCode());
            }
        } finally {
            httpURLConnection.disconnect();
            stopWatch.logEvent("Done");
            LogUtil.i(HttpRequest.class.getName(), stopWatch.getSummary());
        }
        return response;
    }

    public void setHeader(String header, String value) {
        if (value != null) {
            mHeaders.put(header, value);
        } else {
            mHeaders.remove(header);
        }
    }
}
