package se.devscout.android.model;

public interface CategoryProperties extends ServerObjectProperties{
    String getName();

//    UUID getUniqueID();

    String getGroup();

    MediaKey getIconMediaKey();

    Integer getActivitiesCount();
}
