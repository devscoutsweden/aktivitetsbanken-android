package se.devscout.server.api.model;

import java.net.URI;

public interface MediaProperties extends ServerObjectProperties{
    URI getURI();

    String getMimeType();
}
