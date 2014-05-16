package se.devscout.android.model.repo.sql;

import android.content.Context;
import se.devscout.android.model.ObjectIdentifierPojo;
import se.devscout.android.model.repo.LocalActivity;
import se.devscout.android.model.repo.LocalCategory;
import se.devscout.android.model.repo.LocalReference;
import se.devscout.android.util.PrimitiveFilter;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.ActivityFilterFactory;
import se.devscout.server.api.model.*;

import java.util.ArrayList;
import java.util.List;

public class SQLiteActivityRepo implements ActivityBank {
    private static SQLiteActivityRepo ourInstance;
    private final DatabaseHelper mDatabaseHelper;
    private UserKey mAnonymousUserKey;

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
    public List<LocalActivity> find(String name, Boolean featured) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<LocalActivity> find(ActivityFilter condition) {
        ArrayList<LocalActivity> res = new ArrayList<LocalActivity>();
        for (LocalActivity activity : mDatabaseHelper.readActivities(condition, null)) {
            if (condition instanceof PrimitiveFilter) {
                PrimitiveFilter primitiveFilter = (PrimitiveFilter) condition;
                if (primitiveFilter.matches(activity)) {
                    res.add(activity);
                }
            }
        }
        return res;
    }

    @Override
    public List<? extends Activity> findFavourites(UserKey userKey) {
        if (userKey != null) {
            throw new UnsupportedOperationException("Finding favourites for specific user is not supported. User must be 'null'.");
        }
        return mDatabaseHelper.readActivities(null, mAnonymousUserKey);
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
    }

    @Override
    public void unsetFavourite(ActivityKey activityKey, UserKey userKey) {
        mDatabaseHelper.unsetFavourite(activityKey, mAnonymousUserKey);
    }

    @Override
    public boolean isFavourite(ActivityKey activityKey, UserKey userKey) {
        return mDatabaseHelper.isFavourite(activityKey, mAnonymousUserKey);
    }

    public void resetDatabase(boolean addTestData) {
        mDatabaseHelper.dropDatabase(addTestData);
    }

}
