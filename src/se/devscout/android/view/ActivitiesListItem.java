package se.devscout.android.view;

import se.devscout.android.util.ActivityUtil;
import se.devscout.server.api.model.Activity;
import se.devscout.server.api.model.ActivityRevision;
import se.devscout.server.api.model.Range;

import java.io.Serializable;
import java.net.URI;

public class ActivitiesListItem implements Serializable {
    private String mName;
    private Range<Integer> mParticipants;
    private Range<Integer> mTimeActivity;
    private String mDescription;
    private Long mId;
    private Range<Integer> mAges;
    private URI mCoverMedia;

    public ActivitiesListItem(Activity activity) {
        ActivityRevision src = ActivityUtil.getLatestActivityRevision(activity);
        mName = src.getName();
        mParticipants = src.getParticipants();
        mTimeActivity = src.getTimeActivity();
        mDescription = src.getDescription();
        mAges = src.getAges();
        mId = activity.getId();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Range<Integer> getParticipants() {
        return mParticipants;
    }

    public void setParticipants(Range<Integer> participants) {
        mParticipants = participants;
    }

    public Range<Integer> getTimeActivity() {
        return mTimeActivity;
    }

    public void setTimeActivity(Range<Integer> timeActivity) {
        mTimeActivity = timeActivity;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public Range<Integer> getAges() {
        return mAges;
    }

    public void setAges(Range<Integer> ages) {
        mAges = ages;
    }

    public URI getCoverMedia() {
        return mCoverMedia;
    }

    public void setCoverMedia(URI coverMedia) {
        mCoverMedia = coverMedia;
    }
}