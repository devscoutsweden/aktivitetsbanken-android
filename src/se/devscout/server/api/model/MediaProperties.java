package se.devscout.server.api.model;

import java.net.URI;

public interface MediaProperties {
    /**
     * the media item's id on the server. This is the value used to refer to the
     * category when communicating with the server.
     */
    int getServerId();

    /**
     * returns whether or not the media item (e.g. photo) has not yet been
     * published to the server, meaning that it is has not be uploaded yet.
     */
    boolean isPublishable();

    URI getURI();

    String getMimeType();
}
