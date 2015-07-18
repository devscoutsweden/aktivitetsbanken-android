package se.devscout.android.model;

import java.net.URI;

public interface ReferenceProperties extends ServerObjectProperties{
    URI getURI();

    String getDescription();
}
