package se.devscout.android.model.repo;

import android.content.Context;
import se.devscout.android.model.ActivityBean;
import se.devscout.android.util.PrimitiveActivityFilterFactory;
import se.devscout.android.util.SimpleFilter;
import se.devscout.android.util.http.UnauthorizedException;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.ActivityBankListener;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.ActivityFilterFactory;
import se.devscout.server.api.model.*;

import java.net.URI;
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
        SimpleFilter simpleFilter = SimpleFilter.fromActivityFilter();
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
    public ActivityList readActivities(ActivityKey... keys) {
        throw new UnsupportedOperationException();
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
    public Boolean isFavourite(ActivityKey activityKey, UserKey userKey) {
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
    public boolean createAnonymousAPIUser() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateUser(UserKey key, UserProperties properties) throws UnauthorizedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public User readUser(UserKey key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Media readMediaItem(MediaKey key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public URI getMediaItemURI(MediaProperties mediaProperties, int width, int height) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Rating readRating(ActivityKey activityKey, UserKey userKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRating(ActivityKey activityKey, UserKey userKey, int rating) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unsetRating(ActivityKey activityKey, UserKey userKey) {
        throw new UnsupportedOperationException();
    }
}
