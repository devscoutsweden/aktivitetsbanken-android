package se.devscout.android.model.repo;

import android.content.Context;
import se.devscout.android.model.ActivityBean;
import se.devscout.android.util.PrimitiveActivityFilterFactory;
import se.devscout.android.util.SimpleFilter;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.ActivityBankListener;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.ActivityFilterFactory;
import se.devscout.server.api.model.*;

import java.util.ArrayList;
import java.util.List;

public class DemoActivityRepo implements ActivityBank {
    private static DemoActivityRepo ourInstance;
    private final List<ActivityBean> mActivities;

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
    public List<ActivityBean> findActivity(ActivityFilter condition) {
        SimpleFilter simpleFilter = SimpleFilter.fromActivityFilter(condition);
        ArrayList<ActivityBean> res = new ArrayList<ActivityBean>();
        for (ActivityBean activity : mActivities) {
            if (simpleFilter.matches(activity)) {
                res.add(activity);
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
    public ActivityBean readActivity(ActivityKey key) {
        for (ActivityBean activity : mActivities) {
            if (key.getId().equals(activity.getId())) {
                return activity;
            }
        }
        return null;
    }

    @Override
    public Activity readActivityFull(ActivityKey key) {
        return readActivity(key);
    }

    @Override
    public ActivityFilterFactory getFilterFactory() {
        return new PrimitiveActivityFilterFactory();
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

    @Override
    public List<? extends SearchHistory> readSearchHistory(int limit, UserKey userKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SearchHistory createSearchHistory(HistoryProperties<SearchHistoryData> properties, UserKey userKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteSearchHistory(int itemsToKeep) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addListener(ActivityBankListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeListener(ActivityBankListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean createAnonymousAPIUser() {
        throw new UnsupportedOperationException();
    }
}
