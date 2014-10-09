package se.devscout.server.api.model;

public interface ServerObjectProperties extends ServerObjectIdentifier {
    /**
     * the activity's revision number (id) when the activiy's data was fetched
     * from the server. This number (id) is used to check if the information in
     * the app's database is out-of-date (compared to the server).
     */
    long getServerRevisionId();

    /**
     * returns whether or not the activity has not yet been published to the
     * server, meaning that it is a local draft.
     */
    boolean isPublishable();
}
