package se.devscout.android.model.repo;

/**
 * Keeps track of "modification counter", also known as revisions or
 * timestamps.
 * <p/>
 * The general idea is that the returned numbers represent
 * ever-increasing version numbers. The data is used to determine if cached data
 * is stale compared to the master data (the cached data is "tagged" with the
 * modification counter value available when caching the data and the cached
 * data can be used as long as the "master data's" modification counter has not
 * changed).
 */
public interface ModificationCounters {
    long getFavouriteList();

    long getSearchHistory();

    long getActivityHistory();
}
