package se.devscout.android.model.repo;

import android.content.Context;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.*;

import java.util.ArrayList;
import java.util.List;

public class DemoActivityRepo implements ActivityBank {
    private static DemoActivityRepo ourInstance;
    private final List<LocalActivity> mActivities;

    public static DemoActivityRepo getInstance(Context ctx) {
        if (ourInstance == null) {
            ourInstance = new DemoActivityRepo(ctx);
        }
        return ourInstance;
    }


    private DemoActivityRepo(Context ctx) {
        mActivities = TestDataUtil.readXMLTestData(ctx);
    }

    @Override
    public List<LocalActivity> find(String name, Boolean featured) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<LocalActivity> find(ActivityFilter condition) {
        ArrayList<LocalActivity> res = new ArrayList<LocalActivity>();
        for (LocalActivity activity : mActivities) {
            if (condition.matches(activity)) {
                res.add(activity);
            }
        }
        return res;
    }

    @Override
    public List<? extends Activity> findFavourites(UserKey userKey) {
        throw new UnsupportedOperationException();
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
    public LocalActivity read(ActivityKey key) {
        for (LocalActivity activity : mActivities) {
            if (key.getId().equals(activity.getId())) {
                return activity;
            }
        }
        return null;
    }

    @Override
    public Activity readFull(ActivityKey key) {
        return read(key);
    }

    @Override
    public Reference createReference(ActivityKey key, ReferenceProperties properties) {
        throw new UnsupportedOperationException();
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
    public List<Category> readCategories() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Category readCategoryFull(CategoryKey key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFavourite(ActivityKey activityKey, UserKey userKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unsetFavourite(ActivityKey activityKey, UserKey userKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFavourite(ActivityKey activityKey, UserKey userKey) {
        throw new UnsupportedOperationException();
    }
}
