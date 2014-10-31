package se.devscout.server.api.model;

public interface CategoryProperties extends ServerObjectProperties{
    String getName();

//    UUID getUniqueID();

    String getGroup();

    MediaKey getIconMediaKey();
}
