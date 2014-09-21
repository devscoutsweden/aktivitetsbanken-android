package se.devscout.server.api.model;

import java.util.Date;
import java.util.List;

public interface ActivityProperties {
    int getServerId();

    int getServerRevisionId();

    User getOwner();

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
