package se.devscout.server.api.model;

import java.net.URI;

public interface MediaProperties {
    Status getStatus();

    URI getURI();

    String getMimeType();
}
