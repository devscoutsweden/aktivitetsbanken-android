package se.devscout.android.util.http;

import java.util.List;
import java.util.Map;

public interface ResponseHeadersValidator {
    void validate(Map<String, List<String>> headers) throws HeaderException;

}
