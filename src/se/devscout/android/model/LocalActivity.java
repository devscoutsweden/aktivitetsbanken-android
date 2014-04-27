package se.devscout.android.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import se.devscout.server.api.model.*;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonFilter("LocalActivity")
class LocalActivity extends ActivityPropertiesPojo implements Activity, ActivityRevision, Serializable, Comparable<LocalActivity> {
    public static long debugCounter;
    private List<LocalActivityRevision> mRevisions = new ArrayList<LocalActivityRevision>();
    private LocalUser mOwner;
    private Long mId;

    LocalActivity(LocalUser owner, Long id) {
        super(owner);
        mId = id;
    }


    @Override
    public List<LocalActivityRevision> getRevisions() {
        return mRevisions;
    }

    private LocalActivityRevision getLatestRevisions() {
        return mRevisions.get(mRevisions.size() - 1);
    }

    public void addRevisions(LocalActivityRevision revision) {
        mRevisions.add(revision);
    }

    @Override
    public Status getStatus() {
        return Status.PUBLISHED;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public String getName() {
        return getLatestRevisions().getName();
    }

    @Override
    public Date getDatePublished() {
        return getLatestRevisions().getDatePublished();
    }

    @Override
    public Date getDateCreated() {
        return getLatestRevisions().getDateCreated();
    }

    @Override
    public String getDescriptionMaterial() {
        return getLatestRevisions().getDescriptionMaterial();
    }

    @Override
    public String getDescriptionIntroduction() {
        return getLatestRevisions().getDescriptionIntroduction();
    }

    @Override
    public String getDescriptionPreparation() {
        return getLatestRevisions().getDescriptionPreparation();
    }

    @Override
    public String getDescription() {
        return getLatestRevisions().getDescription();
    }

    @Override
    public String getDescriptionSafety() {
        return getLatestRevisions().getDescriptionSafety();
    }

    @Override
    public String getDescriptionNotes() {
        return getLatestRevisions().getDescriptionNotes();
    }

    public void addDescriptionNote(String note) {
        getLatestRevisions().addDescriptionNote(note);
    }

    @Override
    public Range<Integer> getAges() {
        return getLatestRevisions().getAges();
    }

    @Override
    public Range<Integer> getParticipants() {
        return getLatestRevisions().getParticipants();
    }

    @Override
    public Range<Integer> getTimeActivity() {
        return getLatestRevisions().getTimeActivity();
    }

    @Override
    public Range<Integer> getTimePreparation() {
        return getLatestRevisions().getTimePreparation();
    }

    @Override
    public boolean isFeatured() {
        return getLatestRevisions().isFeatured();
    }

    @Override
    public URI getSourceURI() {
        return getLatestRevisions().getSourceURI();
    }

    @Override
    public User getAuthor() {
        return getLatestRevisions().getAuthor();
    }

    @Override
    public List<LocalMedia> getMediaItems() {
        return getLatestRevisions().getMediaItems();
    }

    public void addMediaItem(URI uri, String mimeType) {
        getLatestRevisions().addMediaItem(uri, mimeType);
    }

    @Override
    public List<LocalReference> getReferences() {
        return getLatestRevisions().getReferences();
    }

    @Override
    public List<LocalCategory> getCategories() {
        return getLatestRevisions().getCategories();
    }

    @Override
    public Media getCoverMedia() {
        return !getMediaItems().isEmpty() ? getMediaItems().get(0) : null;
    }

    @Override
    public User getOwner() {
        return mOwner;
    }

    @Override
    public String toString() {
        return getLatestRevisions().getName();
    }

    public void addCategory(String group, String name) {
        getLatestRevisions().addCategory(group, name);
    }

    public void addReference(URI uri, ReferenceType type) {
        getLatestRevisions().addReference(uri, type);
    }

    public void setMaterial(String material) {
        getLatestRevisions().setMaterial(material);
    }

    public void setTimeActivity(Range<Integer> timeActivity) {
        getLatestRevisions().setTimeActivity(timeActivity);
    }

    public void setParticipants(Range<Integer> participants) {
        getLatestRevisions().setParticipants(participants);
    }

    @Override
    public int compareTo(LocalActivity localActivity) {
        return localActivity != null ? getLatestRevisions().getName().compareTo(localActivity.getLatestRevisions().getName()) : 0;
    }

    public void setSourceURI(URI sourceURI) {
        getLatestRevisions().setSourceURI(sourceURI);
    }

    public void setTimePreparation(Range<Integer> timePreparation) {
        getLatestRevisions().setTimePreparation(timePreparation);
    }

    public void setOwner(LocalUser owner) {
        mOwner = owner;
    }

    public void setAuthor(LocalUser author) {
        getLatestRevisions().setAuthor(author);
    }

    public void setIntroduction(String introduction) {
        getLatestRevisions().setIntroduction(introduction);
    }

    public void setDescription(String description) {
        getLatestRevisions().setDescription(description);
    }

    public void setSafety(String safety) {
        getLatestRevisions().setSafety(safety);
    }

    public void setName(String name) {
        getLatestRevisions().setName(name);
    }

    public void setAges(Range<Integer> ages) {
        getLatestRevisions().setAges(ages);
    }

    @Override
    public ActivityKey getActivityKey() {
        return getLatestRevisions().getActivityKey();
    }

    @Override
    public Long getId() {
        return mId;
    }

    public void setFeatured(boolean featured) {
        getLatestRevisions().setFeatured(featured);
    }
}
