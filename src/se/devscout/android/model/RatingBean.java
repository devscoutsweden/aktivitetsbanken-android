package se.devscout.android.model;

import se.devscout.server.api.model.Rating;
import se.devscout.server.api.model.RatingStatus;

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
