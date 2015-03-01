package se.devscout.android.model.repo.sql;

import android.content.Context;
import se.devscout.android.model.*;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.PrimitiveActivityFilterFactory;
import se.devscout.android.util.http.UnauthorizedException;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.ActivityBankListener;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.ActivityFilterFactory;
import se.devscout.server.api.model.*;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class SQLiteActivityRepo implements ActivityBank {
    protected final DatabaseHelper mDatabaseHelper;
    private List<ActivityBankListener> mListeners = new ArrayList<ActivityBankListener>();
    private static SQLiteActivityRepo ourInstance;

    public static SQLiteActivityRepo getInstance(Context ctx) {
        if (ourInstance == null) {
            ourInstance = new SQLiteActivityRepo(ctx);
        }
        return ourInstance;
    }

    public SQLiteActivityRepo(Context ctx) {
        mDatabaseHelper = new DatabaseHelper(ctx);
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
    public ActivityProperties updateActivity(ActivityKey key, ActivityProperties properties) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ActivityList readActivities(ActivityKey... keys) throws UnauthorizedException {
        return mDatabaseHelper.readActivities(keys);
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
    public List<CategoryBean> readCategories() throws UnauthorizedException {
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

    private void fireSearchHistoryItemAdded(SearchHistory searchHistoryItem) {
        for (ActivityBankListener listener : mListeners) {
            listener.onSearchHistoryItemAdded(searchHistoryItem);
        }
    }

    private void fireFavouriteChange(ActivityKey activityKey, UserKey userKey, boolean isFavouriteNow) {
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

    protected void fireAsyncException(Exception e) {
        for (ActivityBankListener listener : mListeners) {
            listener.onAsyncException(e);
        }
    }

    public void resetDatabase(boolean addTestData) {
        mDatabaseHelper.dropDatabase(addTestData);
    }

    public boolean setAnonymousUserAPIKey(String apiKey, UserKey userKey) {
        return mDatabaseHelper.updateUserAPIKey(apiKey, userKey);
    }
}
