package se.devscout.android.model;

import java.net.URI;

public interface MediaProperties extends ServerObjectProperties{
    URI getURI();

    String getMimeType();
}
