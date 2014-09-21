package se.devscout.android.model.repo.sql;

import android.content.Context;
import android.util.Log;
import se.devscout.android.model.ObjectIdentifierPojo;
import se.devscout.android.model.repo.LocalActivity;
import se.devscout.android.model.repo.LocalCategory;
import se.devscout.android.model.repo.LocalReference;
import se.devscout.android.model.repo.LocalSearchHistory;
import se.devscout.android.util.SimpleFilter;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.ActivityBankListener;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.ActivityFilterFactory;
import se.devscout.server.api.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class SQLiteActivityRepo implements ActivityBank {
    protected final DatabaseHelper mDatabaseHelper;
    private UserKey mAnonymousUserKey;
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
        mAnonymousUserKey = new ObjectIdentifierPojo(mDatabaseHelper.getAnonymousUserId());
    }

    @Override
    public List<LocalActivity> find(ActivityFilter condition) {
        ArrayList<LocalActivity> res = new ArrayList<LocalActivity>();
        for (LocalActivity activity : mDatabaseHelper.readActivities(condition)) {
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
    public Activity create(ActivityProperties properties) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(ActivityKey key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ActivityProperties update(ActivityKey key, ActivityProperties properties) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Activity read(ActivityKey key) {
        return mDatabaseHelper.readActivity(key);
    }

    @Override
    public Activity readFull(ActivityKey key) {
        return read(key);
    }

    @Override
    public ActivityFilterFactory getFilterFactory() {
        return new SQLActivityFilterFactory(new ObjectIdentifierPojo(mDatabaseHelper.getAnonymousUserId()));
    }

    @Override
    public Reference createReference(ActivityKey key, ReferenceProperties properties) {
        long id = mDatabaseHelper.createReference(properties);
        return new LocalReference(id, properties.getType(), properties.getURI());
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
    public List<LocalCategory> readCategories() {
        return mDatabaseHelper.readCategories();
    }

    @Override
    public Category readCategoryFull(CategoryKey key) {
        return mDatabaseHelper.readCategory(key);
    }

    @Override
    public void setFavourite(ActivityKey activityKey, UserKey userKey) {
        mDatabaseHelper.setFavourite(activityKey, mAnonymousUserKey);
        fireFavouriteChange(activityKey, userKey, true);
    }

    @Override
    public void unsetFavourite(ActivityKey activityKey, UserKey userKey) {
        mDatabaseHelper.unsetFavourite(activityKey, mAnonymousUserKey);
        fireFavouriteChange(activityKey, userKey, false);
    }

    @Override
    public boolean isFavourite(ActivityKey activityKey, UserKey userKey) {
        return mDatabaseHelper.isFavourite(activityKey, mAnonymousUserKey);
    }

    @Override
    public List<? extends SearchHistory> readSearchHistory(int limit) {
        List<LocalSearchHistory> items = mDatabaseHelper.readSearchHistory(mAnonymousUserKey, true, limit, true);
        return new ArrayList<SearchHistory>(new LinkedHashSet<LocalSearchHistory>(items));
    }

    @Override
    public SearchHistory createSearchHistory(HistoryProperties<SearchHistoryData> properties) {
        try {
            mDatabaseHelper.createSearchHistoryItem(properties);
            fireSearchHistoryItemAdded(null);
        } catch (IOException e) {
            Log.e(SQLiteActivityRepo.class.getName(), "Could not create search history entry", e);
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

}
