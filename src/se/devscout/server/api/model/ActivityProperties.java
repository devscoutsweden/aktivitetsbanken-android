package se.devscout.server.api.model;

import java.util.Date;
import java.util.List;

public interface ActivityProperties {
    /**
     * the activity's id on the server. This is the value used to refer to the
     * category when communicating with the server.
     */
    int getServerId();

    /**
     * the activity's revision number (id) when the activiy's data was fetched
     * from the server. This number (id) is used to check if the information in
     * the app's database is out-of-date (compared to the server).
     */
    int getServerRevisionId();

    User getOwner();

    /**
     * returns whether or not the activity has not yet been published to the
     * server, meaning that it is a local draft.
     */
    boolean isPublishable();

    String getName();

    Date getDatePublished();

    Date getDateCreated();

    String getDescriptionMaterial();

    String getDescriptionIntroduction();

    String getDescriptionPreparation();

    String getDescription();

    String getDescriptionSafety();

    String getDescriptionNotes();

    Range<Integer> getAges();

    Range<Integer> getParticipants();

    Range<Integer> getTimeActivity();

    Range<Integer> getTimePreparation();

    boolean isFeatured();

    List<? extends Media> getMediaItems();

    List<? extends Reference> getReferences();

    List<? extends Category> getCategories();

    Media getCoverMedia();
}
