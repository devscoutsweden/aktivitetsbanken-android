package se.devscout.server.api.model;

public interface CategoryProperties {
    /**
     * the category's id on the server. This is the value used to refer to the
     * category when communicating with the server.
     */
    int getServerId();

    String getName();

//    UUID getUniqueID();

    String getGroup();
}
