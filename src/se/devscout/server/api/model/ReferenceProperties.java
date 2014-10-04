package se.devscout.server.api.model;

import java.net.URI;

public interface ReferenceProperties extends ServerObjectProperties{
    URI getURI();

    ReferenceType getType();
}
