package se.devscout.android.model;

import java.util.Date;
import java.util.List;

public interface ActivityProperties extends ServerObjectProperties {
    UserKey getOwner();

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

    Integer getFavouritesCount();

    Double getRatingAverage();

    /**
     * @return A list of related keys for related activities. Empty list if
     * there are no related activities. Null if it is not known if the activity
     * has any related activities, so there is an subtle but important
     * difference between returning null and an empty list.
     */
    List<ActivityKey> getRelatedActivitiesKeys();
}
