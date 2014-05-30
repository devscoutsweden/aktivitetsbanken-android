package se.devscout.server.api;

import se.devscout.server.api.model.*;

import java.util.List;

public interface ActivityBank {

    List<? extends Activity> find(ActivityFilter condition);

    Activity create(ActivityProperties properties);

    void delete(ActivityKey key);

    ActivityProperties update(ActivityKey key, ActivityProperties properties);

    ActivityProperties read(ActivityKey key);

    Activity readFull(ActivityKey key);

    ActivityFilterFactory getFilterFactory();

    Reference createReference(ActivityKey key, ReferenceProperties properties);

    void deleteReference(ActivityKey key, ReferenceKey referenceKey);

    List<? extends Reference> readReferences(ActivityKey key);

    List<? extends Category> readCategories();

    Category readCategoryFull(CategoryKey key);

    void setFavourite(ActivityKey activityKey, UserKey userKey);

    void unsetFavourite(ActivityKey activityKey, UserKey userKey);

    boolean isFavourite(ActivityKey activityKey, UserKey userKey);

    List<? extends SearchHistory> readSearchHistory(int limit);

    SearchHistory createSearchHistory(HistoryProperties<SearchHistoryData> properties);

    void deleteSearchHistory(int itemsToKeep);

    void addListener(ActivityBankListener listener);

    void removeListener(ActivityBankListener listener);
}
