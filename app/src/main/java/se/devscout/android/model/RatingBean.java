package se.devscout.android.model;

public class RatingBean extends RatingPropertiesBean implements Rating {
    private Long mActivityId;
    private Long mUserId;

    public RatingBean(int rating, RatingStatus status, Long activityId, Long userId) {
        super(rating, status);
        mActivityId = activityId;
        mUserId = userId;
    }

    @Override
    public Long getActivityId() {
        return mActivityId;
    }

    @Override
    public Long getUserId() {
        return mUserId;
    }
}
