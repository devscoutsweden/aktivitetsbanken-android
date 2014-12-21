package se.devscout.android.model.repo.sql;

import android.content.Context;
import se.devscout.android.model.ActivityBean;
import se.devscout.android.model.CategoryBean;
import se.devscout.android.model.ReferenceBean;
import se.devscout.android.model.SearchHistoryBean;
import se.devscout.android.util.IdentityProvider;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.SimpleFilter;
import se.devscout.android.util.concurrency.BackgroundTasksHandlerThread;
import se.devscout.android.util.http.UnauthorizedException;
import se.devscout.server.api.*;
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
        ArrayList<ActivityBean> res = new ArrayList<ActivityBean>();
        for (ActivityBean activity : mDatabaseHelper.readActivities(condition)) {
            if (condition instanceof SimpleFilter) {
                SimpleFilter simpleFilter = (SimpleFilter) condition;
                if (simpleFilter.matches(activity)) {
                    res.add(activity);
                }
            }
        }
        return res;
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
    public void readActivityAsync(ActivityKey key, OnReadDoneCallback<Activity> callback, BackgroundTasksHandlerThread tasksHandlerThread) {
        if (callback != null) {
            ActivityBean activity = mDatabaseHelper.readActivities(key).get(0);
            callback.onRead(activity);
        }
    }

    @Override
    public List<ActivityBean> readActivities(ActivityKey... keys) throws UnauthorizedException {
        return mDatabaseHelper.readActivities(keys);
    }

    @Override
    public ActivityFilterFactory getFilterFactory() {
        return new SQLActivityFilterFactory();
    }

    @Override
    public Reference createReference(ActivityKey key, ReferenceProperties properties) {
        long id = mDatabaseHelper.createReference(properties);
        return new ReferenceBean(id,properties.getServerId(), properties.getServerRevisionId(), properties.getURI(), null);
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
    public void setFavourite(ActivityKey activityKey, UserKey userKey) {
        mDatabaseHelper.setFavourite(activityKey, userKey);
        fireFavouriteChange(activityKey, userKey, true);
    }

    @Override
    public void unsetFavourite(ActivityKey activityKey, UserKey userKey) {
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
        mListeners.add(listener);
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
    public Media readMediaItem(MediaKey key) {
        return mDatabaseHelper.readMedia(key);
    }

    @Override
    public URI getMediaItemURI(MediaProperties mediaProperties, int width, int height) {
        return mediaProperties.getURI();
    }

    @Override
    public void logIn(IdentityProvider provider, String data) {
        //No need to authenticate user. Do nothing.
    }

    @Override
    public void logOut() {
        //User cannot log in, to logging out is a no-brainer.
    }

    @Override
    public boolean isLoggedIn() {
        return true;
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

    public void resetDatabase(boolean addTestData) {
        mDatabaseHelper.dropDatabase(addTestData);
    }

    public boolean setAnonymousUserAPIKey(String apiKey, UserKey userKey) {
        return mDatabaseHelper.updateUserAPIKey(apiKey, userKey);
    }
}
