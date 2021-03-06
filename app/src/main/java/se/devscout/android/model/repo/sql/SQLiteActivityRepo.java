package se.devscout.android.model.repo.sql;

import android.content.Context;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import se.devscout.android.model.Activity;
import se.devscout.android.model.ActivityBean;
import se.devscout.android.model.ActivityHistory;
import se.devscout.android.model.ActivityHistoryData;
import se.devscout.android.model.ActivityKey;
import se.devscout.android.model.ActivityList;
import se.devscout.android.model.ActivityProperties;
import se.devscout.android.model.Category;
import se.devscout.android.model.CategoryBean;
import se.devscout.android.model.CategoryKey;
import se.devscout.android.model.HistoryProperties;
import se.devscout.android.model.Media;
import se.devscout.android.model.MediaKey;
import se.devscout.android.model.MediaProperties;
import se.devscout.android.model.ObjectIdentifierBean;
import se.devscout.android.model.Rating;
import se.devscout.android.model.RatingPropertiesBean;
import se.devscout.android.model.RatingStatus;
import se.devscout.android.model.Reference;
import se.devscout.android.model.ReferenceBean;
import se.devscout.android.model.ReferenceKey;
import se.devscout.android.model.ReferenceProperties;
import se.devscout.android.model.SearchHistory;
import se.devscout.android.model.SearchHistoryBean;
import se.devscout.android.model.SearchHistoryData;
import se.devscout.android.model.SystemMessageBean;
import se.devscout.android.model.User;
import se.devscout.android.model.UserKey;
import se.devscout.android.model.UserProfileBean;
import se.devscout.android.model.UserProperties;
import se.devscout.android.model.activityfilter.ActivityFilter;
import se.devscout.android.model.activityfilter.ActivityFilterFactory;
import se.devscout.android.model.repo.ActivityBank;
import se.devscout.android.model.repo.ActivityBankListener;
import se.devscout.android.model.repo.ModificationCounters;
import se.devscout.android.model.repo.remote.OfflineException;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.PrimitiveActivityFilterFactory;
import se.devscout.android.util.auth.CredentialsManager;
import se.devscout.android.util.http.UnauthorizedException;

public class SQLiteActivityRepo implements ActivityBank {
    protected final DatabaseHelper mDatabaseHelper;
    private final Context mContext;
    private List<ActivityBankListener> mListeners = new ArrayList<ActivityBankListener>();
    private static SQLiteActivityRepo ourInstance;

    private SQLiteModificationCounters mModificationCounters;

    public static SQLiteActivityRepo getInstance(Context ctx) {
        if (ourInstance == null) {
            ourInstance = new SQLiteActivityRepo(ctx);
        }
        return ourInstance;
    }

    public SQLiteActivityRepo(Context ctx) {
        mContext = ctx;
        mDatabaseHelper = new DatabaseHelper(mContext);
        mModificationCounters = new SQLiteModificationCounters();
    }

    @Override
    public List<ActivityBean> findActivity(ActivityFilter condition) throws UnauthorizedException {
        return new ArrayList<ActivityBean>(mDatabaseHelper.readActivities(condition));
    }

    @Override
    public Activity createActivity(ActivityProperties properties) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteActivity(ActivityKey key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateActivity(ActivityKey key, ActivityProperties properties) throws UnauthorizedException {
        if (!mDatabaseHelper.updateActivity(key, properties)) {
            LogUtil.e(SQLiteActivityRepo.class.getName(), "Could not update activity");
        }
    }

    @Override
    public ActivityList readActivities(ActivityKey... keys) throws UnauthorizedException {
        return mDatabaseHelper.readActivities(keys);
    }

    @Override
    public List<? extends Activity> readRelatedActivities(ActivityKey primaryActivity, List<? extends ActivityKey> forcedRelatedActivities) throws UnauthorizedException {
        // Read activities forcedRelatedActivities. Caches activities will be returned without network traffic. Non-cached activities will be fetched from the server API.

        if (forcedRelatedActivities != null) {
            ActivityList relatedActivities = readActivities(forcedRelatedActivities.toArray(new ActivityKey[forcedRelatedActivities.size()]));
            ArrayList<ActivityKey> relatedKeys = new ArrayList<>();
            for (Activity relatedActivity : relatedActivities) {
                relatedKeys.add(new ObjectIdentifierBean(relatedActivity.getId()));
            }

            mDatabaseHelper.setRelatedActivities(primaryActivity, relatedKeys);
        }
        return mDatabaseHelper.readRelatedActivities(primaryActivity);
    }

    @Override
    public ActivityFilterFactory getFilterFactory() {
        return new PrimitiveActivityFilterFactory();
    }

    @Override
    public Reference createReference(ActivityKey key, ReferenceProperties properties) {
        long id = mDatabaseHelper.createReference(properties);
        return new ReferenceBean(id, properties.getServerId(), properties.getServerRevisionId(), properties.getURI(), null);
    }

    @Override
    public void deleteReference(ActivityKey key, ReferenceKey referenceKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Reference> readReferences(ActivityKey key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<CategoryBean> readCategories(int minActivitiesCount) throws UnauthorizedException {
        return mDatabaseHelper.readCategories();
    }

    @Override
    public Category readCategoryFull(CategoryKey key) {
        return mDatabaseHelper.readCategory(key);
    }

    @Override
    public void setFavourite(ActivityKey activityKey, UserKey userKey) throws UnauthorizedException {
        mDatabaseHelper.setFavourite(activityKey, userKey);
        fireFavouriteChange(activityKey, userKey, true);
    }

    @Override
    public void unsetFavourite(ActivityKey activityKey, UserKey userKey) throws UnauthorizedException {
        mDatabaseHelper.unsetFavourite(activityKey, userKey);
        fireFavouriteChange(activityKey, userKey, false);
    }

    @Override
    public Boolean isFavourite(ActivityKey activityKey, UserKey userKey) {
        return mDatabaseHelper.isFavourite(activityKey, userKey);
    }

    @Override
    public List<? extends Activity> readActivityHistory(int limit, UserKey userKey) {
        return mDatabaseHelper.readActivityHistory(userKey, true, limit, true);
    }

    @Override
    public ActivityHistory createActivityHistory(HistoryProperties<ActivityHistoryData> properties, UserKey userKey) {
        try {
            mDatabaseHelper.createActivityHistoryItem(properties, userKey);
            mModificationCounters.touch(SQLiteModificationCounters.Key.ACTIVITY_HISTORY);
            LogUtil.i(SQLiteActivityRepo.class.getName(), "Saved " + properties.getData().getId() + " in activity history.");
        } catch (IOException e) {
            LogUtil.e(SQLiteActivityRepo.class.getName(), "Could not create activity history entry", e);
        }
        return null;
    }

    @Override
    public List<? extends SearchHistory> readSearchHistory(int limit, UserKey userKey) {
        List<SearchHistoryBean> items = mDatabaseHelper.readSearchHistory(userKey, true, limit, true);
        return new ArrayList<SearchHistory>(new LinkedHashSet<SearchHistoryBean>(items));
    }

    @Override
    public SearchHistory createSearchHistory(HistoryProperties<SearchHistoryData> properties, UserKey userKey) {
        try {
            mDatabaseHelper.createSearchHistoryItem(properties, userKey);
            fireSearchHistoryItemAdded(null);
        } catch (IOException e) {
            LogUtil.e(SQLiteActivityRepo.class.getName(), "Could not create search history entry", e);
        }
        return null;
    }

    @Override
    public void deleteSearchHistory(int itemsToKeep) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addListener(ActivityBankListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    @Override
    public void removeListener(ActivityBankListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public boolean createAnonymousAPIUser() {
        // TODO: implement this for when client is offline the first time an API call is made
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateUser(UserKey key, UserProperties properties) throws UnauthorizedException {
        mDatabaseHelper.updateUser(key, properties);
    }

    @Override
    public User readUser(UserKey key) {
        return mDatabaseHelper.readUser(key);
    }

    @Override
    public UserProfileBean readUserProfile() {
        User user = readUser(CredentialsManager.getInstance(mContext).getCurrentUser());
        return new UserProfileBean(user.getDisplayName(), user.getAPIKey(), user.getId(), user.getServerId(), user.getServerRevisionId(), null, user.getRole());
    }

    @Override
    public void updateUserProfile(UserProperties properties) throws UnauthorizedException, OfflineException {
        updateUser(CredentialsManager.getInstance(mContext).getCurrentUser(), properties);
    }

    @Override
    public Media readMediaItem(MediaKey key) {
        return mDatabaseHelper.readMedia(key);
    }

    @Override
    public URI getMediaItemURI(MediaProperties mediaProperties, int width, int height) {
        return mediaProperties.getURI();
    }

    @Override
    public Rating readRating(ActivityKey activityKey, UserKey userKey) {
        return mDatabaseHelper.readRating(activityKey, userKey);
    }

    @Override
    public void setRating(ActivityKey activityKey, UserKey userKey, int rating) {
        mDatabaseHelper.setRating(activityKey, userKey, new RatingPropertiesBean(rating, RatingStatus.CHANGED));
    }

    @Override
    public void unsetRating(ActivityKey activityKey, UserKey userKey) {
        mDatabaseHelper.removeRating(activityKey, userKey);
    }

    @Override
    public ModificationCounters getModificationCounters() {
        return mModificationCounters;
    }

    @Override
    public List<String> getSystemMessages(String key) {
        ArrayList<String> values = new ArrayList<>();
        List<SystemMessageBean> systemMessages = mDatabaseHelper.readSystemMessages();
        for (SystemMessageBean systemMessage : systemMessages) {
            if (systemMessage.getKey().equalsIgnoreCase(key)) {
                Date now = new Date();
                boolean isAfterMessageStart = systemMessage.getValidFrom() == null || systemMessage.getValidFrom().before(now);
                boolean isBeforeMessageEnd = systemMessage.getValidTo() == null || systemMessage.getValidTo().after(now);
                if (isAfterMessageStart && isBeforeMessageEnd) {
                    values.add(systemMessage.getValue());
                }
            }
        }
        return values;
    }

    private void fireSearchHistoryItemAdded(SearchHistory searchHistoryItem) {
        mModificationCounters.touch(SQLiteModificationCounters.Key.SEARCH_HISTORY);
        for (ActivityBankListener listener : mListeners) {
            listener.onSearchHistoryItemAdded(searchHistoryItem);
        }
    }

    private void fireFavouriteChange(ActivityKey activityKey, UserKey userKey, boolean isFavouriteNow) {
        mModificationCounters.touch(SQLiteModificationCounters.Key.FAVOURITE_LIST);
        for (ActivityBankListener listener : mListeners) {
            listener.onFavouriteChange(activityKey, userKey, isFavouriteNow);
        }
    }

/*
    protected void fireLoggedIn() {
        for (ActivityBankListener listener : mListeners) {
            listener.onLogIn();
        }
    }

    protected void fireLoggedOut() {
        for (ActivityBankListener listener : mListeners) {
            listener.onLogOut();
        }
    }
*/

    protected void fireServiceDegradation(String message, Exception e) {
        for (ActivityBankListener listener : mListeners) {
            listener.onServiceDegradation(message, e);
        }
    }

    public void resetDatabase(boolean addTestData) {
        mDatabaseHelper.dropDatabase(addTestData);
    }

    public boolean setAnonymousUserAPIKey(String apiKey, UserKey userKey) {
        return mDatabaseHelper.updateUserAPIKey(apiKey, userKey);
    }

    private static class SQLiteModificationCounters implements ModificationCounters {

        private static enum Key {
            FAVOURITE_LIST,
            SEARCH_HISTORY,
            ACTIVITY_HISTORY
        }

        private Map<Key, Long> counters = new HashMap<>();

        private SQLiteModificationCounters() {
            for (Key key : Key.values()) {
                touch(key);
            }
        }

        @Override
        public long getFavouriteList() {
            return get(Key.FAVOURITE_LIST);
        }

        @Override
        public long getSearchHistory() {
            return get(Key.SEARCH_HISTORY);
        }

        @Override
        public long getActivityHistory() {
            return get(Key.ACTIVITY_HISTORY);
        }

        private synchronized long get(Key key) {
            return counters.get(key).longValue();
        }

        private synchronized void touch(Key key) {
            counters.put(key, System.currentTimeMillis());
        }
    }
}
