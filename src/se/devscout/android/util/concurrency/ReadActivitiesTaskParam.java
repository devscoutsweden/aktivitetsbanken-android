package se.devscout.android.util.concurrency;

import se.devscout.server.api.model.ActivityKey;

/**
 * Interface used by the scheduled task in order to retrieve an up-to-date
 * list of activity identifiers. This enables ActivityBank implementations to
 * delay the decision of which activities to actually request from the API until
 * the very last moment, as opposed to having to decide then the task is queued.
 * The up-side of this is that multiple "read requests" can be grouped/batched
 * at a  later point in time if the background task executor happens to be busy
 * when the "read requests" were issued.
 */
public interface ReadActivitiesTaskParam {
    ActivityKey[] getKeys();
}
