package se.devscout.android.model;

import se.devscout.server.api.model.RatingProperties;
import se.devscout.server.api.model.RatingStatus;

public class RatingPropertiesBean implements RatingProperties {
    private RatingStatus mStatus;

    private int mRating;

    public RatingPropertiesBean(int rating, RatingStatus status) {
        mRating = rating;
        mStatus = status;
    }

    public void setRating(int rating) {
        mRating = rating;
    }

    public void setStatus(RatingStatus status) {
        mStatus = status;
    }

    @Override
    public RatingStatus getStatus() {
        return mStatus;
    }

    @Override
    public int getRating() {
        return mRating;
    }
}
