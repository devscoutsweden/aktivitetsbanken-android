package se.devscout.server.api;

import se.devscout.server.api.model.*;

import java.util.List;

public interface ActivityBank {
    List<? extends Activity> find(String name, Boolean featured);

    List<? extends Activity> find(ActivityFilter condition);

    List<? extends Activity> findFavourites(UserKey userKey);

    Activity create(ActivityProperties properties);

    void delete(ActivityKey key);

    ActivityProperties update(ActivityKey key, ActivityProperties properties);

    ActivityProperties read(ActivityKey key);

    Activity readFull(ActivityKey key);

    Reference createReference(ActivityKey key, ReferenceProperties properties);

    void deleteReference(ActivityKey key, ReferenceKey referenceKey);

    List<? extends Reference> readReferences(ActivityKey key);

    List<? extends Category> readCategories();

    Category readCategoryFull(CategoryKey key);

    void setFavourite(ActivityKey activityKey, UserKey userKey);

    void unsetFavourite(ActivityKey activityKey, UserKey userKey);

    boolean isFavourite(ActivityKey activityKey, UserKey userKey);
}
