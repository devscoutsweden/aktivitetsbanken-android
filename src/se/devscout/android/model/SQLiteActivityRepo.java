package se.devscout.android.model;

import android.content.Context;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.*;

import java.util.ArrayList;
import java.util.List;

public class SQLiteActivityRepo implements ActivityBank {
    private static SQLiteActivityRepo ourInstance;
    private final DatabaseHelper mDatabaseHelper;

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
    public List<LocalActivity> find(String name, Boolean featured) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<LocalActivity> find(ActivityFilter condition) {
        ArrayList<LocalActivity> res = new ArrayList<LocalActivity>();
        for (LocalActivity activity : mDatabaseHelper.readActivities()) {
            if (condition.matches(activity)) {
                res.add(activity);
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

    public void resetDatabase(boolean addTestData) {
        mDatabaseHelper.dropDatabase(addTestData);
    }

}
