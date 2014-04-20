package se.devscout.android.model;

import se.devscout.shared.data.model.*;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LocalActivity implements ActivityCurrent, Activity, ActivityRevision, Serializable, Comparable<LocalActivity> {
    private String mName;
    private Date mDatePublished = new Date();
    private Date mDateCreated = new Date();
    private String mIntroduction;
    private String mPreparation;
    private String mDescription;
    private String mSafety;
    private boolean mFeatured;
    private Integer mId;
    private Range<Integer> mAges;
    private List<LocalCategory> mCategories = new ArrayList<LocalCategory>();
    private StringBuilder mNotes = new StringBuilder();
    private String mMaterial;
    private Range<Integer> mTimeActivity;
    private Range<Integer> mParticipants;
    private List<LocalMedia> mMediaItems = new ArrayList<LocalMedia>();

    public LocalActivity(String name, int featuredImageResId, boolean featured, Integer id) {
        mName = name;
        if (featuredImageResId >= 0) {
        }
        mId = id;
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
    public List<ActivityRevision> getRevisions() {
        return Collections.singletonList((ActivityRevision) this);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFeatured() {
        return mFeatured;
    }

    @Override
    public URI getSourceURI() {
        throw new UnsupportedOperationException();
    }

    @Override
    public User getAuthor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<LocalMedia> getMediaItems() {
        return mMediaItems;
    }

    public void addMediaItem(URI uri) {
        mMediaItems.add(new LocalMedia(uri));
    }

    @Override
    public List<Reference> getReferences() {
        throw new UnsupportedOperationException();
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
    public User getOwner() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getId() {
        return mId;
    }

    @Override
    public ActivityKey getActivityKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return mName;
    }

    public void addCategory(String group, String name) {
        mCategories.add(new LocalCategory(group, name));
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

    @Override
    public int compareTo(LocalActivity localActivity) {
        return localActivity != null ? mName.compareTo(localActivity.mName) : 0;
    }
}
