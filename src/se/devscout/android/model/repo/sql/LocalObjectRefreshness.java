package se.devscout.android.model.repo.sql;

/**
 * Constants for various levels of "data freshness", meaning how up-to-date the
 * locally stored data is compared to the data on the remote server.
 */
public enum LocalObjectRefreshness {
    /**
     * Local copy is older than server version.
     */
    LOCAL_IS_OLD,
    /**
     * Local copy is identical to server version.
     */
    LOCAL_IS_UP_TO_DATE,
    /**
     * There is no local copy of the server data.
     */
    LOCAL_IS_MISSING;
}
