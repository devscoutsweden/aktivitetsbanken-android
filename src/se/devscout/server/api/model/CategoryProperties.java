package se.devscout.server.api.model;

import java.util.UUID;

public interface CategoryProperties {
    String getName();

    UUID getUniqueID();

    String getGroup();
}
