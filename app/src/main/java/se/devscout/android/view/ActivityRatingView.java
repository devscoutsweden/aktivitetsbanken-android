package se.devscout.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import se.devscout.android.R;
import se.devscout.android.model.ActivityKey;
import se.devscout.android.model.Rating;
import se.devscout.android.model.UserKey;
import se.devscout.android.model.repo.ActivityBank;
import se.devscout.android.util.LogUtil;

public class ActivityRatingView extends LinearLayout {
    private ActivityKey mActivityKey;
    private UserKey mUserKey;
    private ActivityBank mActivityBank;

    public ActivityRatingView(Context context, ActivityKey activityKey, UserKey userKey, ActivityBank activityBank) {
        super(context);
        mActivityKey = activityKey;
        mUserKey = userKey;
        mActivityBank = activityBank;
        init(context);
    }

    public ActivityRatingView(Context context, AttributeSet attrs, ActivityKey activityKey, UserKey userKey, ActivityBank activityBank) {
        super(context, attrs);
        mActivityKey = activityKey;
        mUserKey = userKey;
        mActivityBank = activityBank;
        init(context);
    }

    public ActivityRatingView(Context context, AttributeSet attrs, int defStyle, ActivityKey activityKey, UserKey userKey, ActivityBank activityBank) {
        super(context, attrs, defStyle);
        mActivityKey = activityKey;
        mUserKey = userKey;
        mActivityBank = activityBank;
        init(context);
    }

    private void init(final Context context) {
        final LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.activity_rating_card, this, true);

        final RatingBar myRatingBar = (RatingBar) view.findViewById(R.id.myRatingBar);
        final Rating rating = mActivityBank.readRating(mActivityKey, mUserKey);
        int ratingNumber = rating != null ? rating.getRating() : 0;
        myRatingBar.setRating(ratingNumber);

        MyRatingBarListener myRatingBarListener = new MyRatingBarListener();
        myRatingBar.setOnRatingBarChangeListener(myRatingBarListener);
//        myRatingBar.setOnTouchListener(myRatingBarListener);
//        myRatingBar.setOnClickListener(myRatingBarListener);

        view.findViewById(R.id.unsetRatingIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRatingBar.setRating(0);
            }
        });

//        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, isListContentHeight ? ViewGroup.LayoutParams.WRAP_CONTENT : LayoutParams.MATCH_PARENT));
    }

    private class MyRatingBarListener implements /*View.OnTouchListener, */RatingBar.OnRatingBarChangeListener {
//        private boolean mSupressChangeEvent;
//        private float mStartX;
//        private float mTouchMarginOfError = getResources().getDimensionPixelSize(R.dimen.defaultMargin);// TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());

        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//            if (!mSupressChangeEvent) {
            if (rating > 0) {
                mActivityBank.setRating(mActivityKey, mUserKey, (int) rating);
            } else {
                mActivityBank.unsetRating(mActivityKey, mUserKey);
            }
            LogUtil.d(ActivityRatingView.class.getName(), "onRatingChanged");
//            }
        }

/*
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mSupressChangeEvent = false;
                mStartX = motionEvent.getX();
                LogUtil.d(ActivityFragment.class.getName(), "ACTION_DOWN");
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                float delta = motionEvent.getX() - mStartX;
                if (-mTouchMarginOfError < delta && delta < mTouchMarginOfError) {
                    int value = (int) (motionEvent.getX() / view.getHeight()) + 1;
                    // User performed a click, not a drag/slide.
                    final RatingBar ratingBar = (RatingBar) view;
                    LogUtil.d(ActivityFragment.class.getName(), "NON-SLIDE current=" + ratingBar.getRating() + ", clicked=" + value);
                    if ((int) ratingBar.getRating() == value) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ratingBar.setRating(0);
                            }
                        });
                        mSupressChangeEvent = true;
                    }
                }
                LogUtil.d(ActivityFragment.class.getName(), "ACTION_UP Moved " + delta + " pixels (margin of error: " + mTouchMarginOfError + ")");
                if (!mSupressChangeEvent) {
                    LogUtil.d(ActivityFragment.class.getName(), "NO CHANGE");
                }
            }
            return false;
        }
*/
    }

}
