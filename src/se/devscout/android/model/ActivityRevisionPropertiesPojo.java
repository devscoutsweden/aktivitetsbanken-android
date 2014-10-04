package se.devscout.android.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import se.devscout.server.api.model.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonFilter("LocalActivityRevision")
public class ActivityRevisionPropertiesPojo implements ActivityRevisionProperties {
    private String mName;
    private Date mDatePublished = new Date();
    private Date mDateCreated = new Date();
    private String mIntroduction;
    private String mPreparation;
    private String mDescription;
    private String mSafety;
    private boolean mFeatured;
    private Range<Integer> mAges;
    protected List<Category> mCategories = new ArrayList<Category>();
    private StringBuilder mNotes = new StringBuilder();
    private String mMaterial;
    private Range<Integer> mTimeActivity;
    private Range<Integer> mTimePreparation;
    private Range<Integer> mParticipants;
    protected List<Media> mMediaItems = new ArrayList<Media>();
    private URI mSourceURI;
    private User mAuthor;
    protected List<Reference> mReferences = new ArrayList<Reference>();
    private User mOwner;
    private ActivityKey mActivityKey;

    public ActivityRevisionPropertiesPojo() {
    }

    public void setFeatured(boolean featured) {
        mFeatured = featured;
    }

    public ActivityRevisionPropertiesPojo(String name, boolean featured, ActivityKey activityKey) {
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

    public void addDescriptionNote(String note) {
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
    public UserKey getAuthor() {
        return mAuthor;
    }

    @Override
    public List<Media> getMediaItems() {
        return mMediaItems;
    }


    @Override
    public List<Reference> getReferences() {
        return mReferences;
    }

    @Override
    public List<Category> getCategories() {
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

    public void setOwner(User owner) {
        mOwner = owner;
    }

    public void setAuthor(User author) {
        mAuthor = author;
    }

    @Override
    public ActivityKey getActivityKey() {
        return mActivityKey;
    }
}
