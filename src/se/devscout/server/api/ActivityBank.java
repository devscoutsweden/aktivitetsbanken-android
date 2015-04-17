package se.devscout.server.api;

import se.devscout.android.util.http.UnauthorizedException;
import se.devscout.server.api.model.*;

import java.net.URI;
import java.util.List;

public interface ActivityBank {

    Long DEFAULT_USER_ID = 1L; // <-- Must match the primary key id generated by SQLite when the database is initialized

    List<? extends Activity> findActivity(ActivityFilter condition) throws UnauthorizedException;

    Activity createActivity(ActivityProperties properties);

    void deleteActivity(ActivityKey key);

    ActivityProperties updateActivity(ActivityKey key, ActivityProperties properties);

    ActivityList readActivities(ActivityKey... keys) throws UnauthorizedException;

    ActivityFilterFactory getFilterFactory();

    Reference createReference(ActivityKey key, ReferenceProperties properties);

    void deleteReference(ActivityKey key, ReferenceKey referenceKey);

    List<? extends Reference> readReferences(ActivityKey key);

    List<? extends Category> readCategories() throws UnauthorizedException;

    Category readCategoryFull(CategoryKey key);

    void setFavourite(ActivityKey activityKey, UserKey userKey) throws UnauthorizedException;

    void unsetFavourite(ActivityKey activityKey, UserKey userKey) throws UnauthorizedException;

    Boolean isFavourite(ActivityKey activityKey, UserKey userKey);

    List<? extends SearchHistory> readSearchHistory(int limit, UserKey userKey);

    SearchHistory createSearchHistory(HistoryProperties<SearchHistoryData> properties, UserKey userKey);

    void deleteSearchHistory(int itemsToKeep);

    void addListener(ActivityBankListener listener);

    void removeListener(ActivityBankListener listener);

    boolean createAnonymousAPIUser();

    void updateUser(UserKey key, UserProperties properties) throws UnauthorizedException;

    User readUser(UserKey key);

    Media readMediaItem(MediaKey key);

    URI getMediaItemURI(MediaProperties mediaProperties, int width, int height);

    Rating readRating(ActivityKey activityKey, UserKey userKey);

    void setRating(ActivityKey activityKey, UserKey userKey, int rating);

    void unsetRating(ActivityKey activityKey, UserKey userKey);

    /**
     * Returns information about when certain lists/tables/entities of the database was last changed.
     */
    ModificationCounters getModificationCounters();

}
