package se.devscout.android.model;

public interface ServerObjectIdentifier {
    /**
     * the activity's id on the server. This is the value used to refer to the
     * category when communicating with the server.
     */
    long getServerId();
}
