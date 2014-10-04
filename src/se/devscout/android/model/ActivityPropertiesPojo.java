package se.devscout.android.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import se.devscout.server.api.model.*;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonFilter("LocalActivity")
public class ActivityPropertiesPojo extends ServerObjectPropertiesPojo implements ActivityProperties, Serializable {
    private List<ActivityRevision> mRevisions = new ArrayList<ActivityRevision>();
    private UserKey mOwner;

    public ActivityPropertiesPojo(boolean publishable, long serverId, long serverRevisionId, UserKey owner) {
        super(publishable, serverId, serverRevisionId);
        mOwner = owner;
    }
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
    protected List<Reference> mReferences = new ArrayList<Reference>();

    private ActivityKey mActivityKey;

    public void setFeatured(boolean featured) {
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
        return null;
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

    @Override
    public UserKey getOwner() {
        return mOwner;
    }

    public void setOwner(User owner) {
        mOwner = owner;
    }

    public void setDateCreated(Date dateCreated) {
        mDateCreated = dateCreated;
    }
}
