package se.devscout.android.util.http;

import java.util.List;
import java.util.Map;

public class ContentLengthValidator implements ResponseHeadersValidator {

    private final int mMaximumLength;

    public ContentLengthValidator(int maximumLength) {
        mMaximumLength = maximumLength;
    }

    @Override
    public void validate(Map<String, List<String>> headers) throws HeaderException {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("content-length") && entry.getValue().size() == 1) {
                long length = Long.parseLong(entry.getValue().get(0));
                if (length < mMaximumLength) {
                    return;
                } else {
                    throw new ContentTooLongException("Will not download files heavier than " + mMaximumLength + " bytes.", length, mMaximumLength);
                }
            }
        }
        throw new HeaderException("Response header Content-Length is not present (or it has multiple values).");
    }
}
