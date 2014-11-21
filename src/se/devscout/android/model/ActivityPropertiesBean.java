package se.devscout.android.model;

import se.devscout.server.api.model.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ActivityPropertiesBean extends ServerObjectPropertiesBean implements ActivityProperties, Serializable {
    private static final List<String> IMAGE_MIME_TYPES = Arrays.asList("image/jpeg", "image/png");
    private UserKey mOwner;
    private Integer mFavouritesCount;

    public ActivityPropertiesBean(boolean publishable, long serverId, long serverRevisionId, UserKey owner) {
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
    protected List<Reference> mReferences = new ArrayList<Reference>();

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
        if (mMediaItems != null) {
            for (Media media : mMediaItems) {
                if (IMAGE_MIME_TYPES.contains(media.getMimeType())) {
                    return media;
                }
            }
        }
        return null;
    }

    @Override
    public Integer getFavouritesCount() {
        return mFavouritesCount;
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

    public void setTimePreparation(Range<Integer> timePreparation) {
        mTimePreparation = timePreparation;
    }

    @Override
    public UserKey getOwner() {
        return mOwner;
    }

    public void setDateCreated(Date dateCreated) {
        mDateCreated = dateCreated;
    }

    public void setFavouritesCount(Integer favouritesCount) {
        mFavouritesCount = favouritesCount;
    }
}
