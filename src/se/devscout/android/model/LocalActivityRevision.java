package se.devscout.android.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import se.devscout.server.api.model.*;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonFilter("LocalActivityRevision")
public class LocalActivityRevision extends LocalObjectIdentifier implements ActivityRevision, Serializable {
    private String mName;
    private Date mDatePublished = new Date();
    private Date mDateCreated = new Date();
    private String mIntroduction;
    private String mPreparation;
    private String mDescription;
    private String mSafety;
    private boolean mFeatured;
    private Range<Integer> mAges;
    private List<LocalCategory> mCategories = new ArrayList<LocalCategory>();
    private StringBuilder mNotes = new StringBuilder();
    private String mMaterial;
    private Range<Integer> mTimeActivity;
    private Range<Integer> mTimePreparation;
    private Range<Integer> mParticipants;
    private List<LocalMedia> mMediaItems = new ArrayList<LocalMedia>();
    private URI mSourceURI;
    private LocalUser mAuthor;
    private List<LocalReference> mReferences = new ArrayList<LocalReference>();
    private LocalUser mOwner;
    private ActivityKey mActivityKey;

    public LocalActivityRevision() {
    }

    public LocalActivityRevision(String name, boolean featured, Integer id, ActivityKey activityKey) {
        super(id);
        mName = name;
        mActivityKey = activityKey;
        mFeatured = featured;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setIntroduction(String introduction) {
        mIntroduction = introduction;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setAges(Range<Integer> ages) {
        mAges = ages;
    }

    public void setPreparation(String preparation) {
        mPreparation = preparation;
    }

    public void setSafety(String safety) {
        mSafety = safety;
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
        return mName;
    }

    @Override
    public Date getDatePublished() {
        return mDatePublished;
    }

    @Override
    public Date getDateCreated() {
        return mDateCreated;
    }

    @Override
    public String getDescriptionMaterial() {
        return mMaterial;
    }

    @Override
    public String getDescriptionIntroduction() {
        return mIntroduction;
    }

    @Override
    public String getDescriptionPreparation() {
        return mPreparation;
    }

    @Override
    public String getDescription() {
        return mDescription;
    }

    @Override
    public String getDescriptionSafety() {
        return mSafety;
    }

    @Override
    public String getDescriptionNotes() {
        return mNotes.toString();
    }

    public void addDescriptionNode(String note) {
        mNotes.append(note);
    }

    @Override
    public Range<Integer> getAges() {
        return mAges;
    }

    @Override
    public Range<Integer> getParticipants() {
        return mParticipants;
    }

    @Override
    public Range<Integer> getTimeActivity() {
        return mTimeActivity;
    }

    @Override
    public Range<Integer> getTimePreparation() {
        return mTimePreparation;
    }

    @Override
    public boolean isFeatured() {
        return mFeatured;
    }

    @Override
    public URI getSourceURI() {
        return mSourceURI;
    }

    @Override
    public User getAuthor() {
        return mAuthor;
    }

    @Override
    public List<LocalMedia> getMediaItems() {
        return mMediaItems;
    }

    public void addMediaItem(URI uri, String mimeType) {
        mMediaItems.add(new LocalMedia(uri, mimeType, LocalMedia.debugCounter++));
    }

    @Override
    public List<LocalReference> getReferences() {
        return mReferences;
    }

    @Override
    public List<LocalCategory> getCategories() {
        return mCategories;
    }

    @Override
    public Media getCoverMedia() {
        return !mMediaItems.isEmpty() ? mMediaItems.get(0) : null;
    }

    @Override
    public String toString() {
        return mName;
    }

    public void addCategory(String group, String name) {
        mCategories.add(new LocalCategory(group, name, LocalCategory.debugCounter++));
    }

    public void addReference(URI uri, ReferenceType type) {
        mReferences.add(new LocalReference(LocalReference.debugCounter++, type, uri));
    }

    public void setMaterial(String material) {
        mMaterial = material;
    }

    public void setTimeActivity(Range<Integer> timeActivity) {
        mTimeActivity = timeActivity;
    }

    public void setParticipants(Range<Integer> participants) {
        mParticipants = participants;
    }

    public void setSourceURI(URI sourceURI) {
        mSourceURI = sourceURI;
    }

    public void setTimePreparation(Range<Integer> timePreparation) {
        mTimePreparation = timePreparation;
    }

    public void setOwner(LocalUser owner) {
        mOwner = owner;
    }

    public void setAuthor(LocalUser author) {
        mAuthor = author;
    }

    @Override
    public ActivityKey getActivityKey() {
        return mActivityKey;
    }
}
