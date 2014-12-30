package se.devscout.android.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import se.devscout.android.util.LogUtil;

/**
 * http://gmariotti.blogspot.se/2013/09/expand-and-collapse-animation.html
 */
class AnimatedToggleSiblingViewListener implements CompoundButton.OnCheckedChangeListener {

    private int mInitialHeight = -1;
    private int mWidth;

    AnimatedToggleSiblingViewListener(int width) {
        mWidth = width;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        ViewGroup parent = (ViewGroup) compoundButton.getParent();
        int thisPos = parent.indexOfChild(compoundButton);
        int targetViewPos = thisPos - 1;
        final View target = parent.getChildAt(targetViewPos);
        if (target != null) {
            if (mInitialHeight == -1) {
                target.measure(
                        View.MeasureSpec.makeMeasureSpec(mWidth, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                mInitialHeight = target.getMeasuredHeight();
                LogUtil.d(AnimatedToggleSiblingViewListener.class.getName(), "Height set to " + mInitialHeight + " since parent is " + mWidth + " wide.");
            }
            if (isChecked) {
                target.setVisibility(View.VISIBLE);

                ValueAnimator mAnimator = slideAnimator(0, mInitialHeight, target);
                mAnimator.start();

            } else {
                ValueAnimator mAnimator = slideAnimator(mInitialHeight, 0, target);

                mAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        target.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
                mAnimator.start();
            }
        }
    }

    private ValueAnimator slideAnimator(int start, int end, final View target) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = target.getLayoutParams();
                layoutParams.height = value;
                target.requestLayout();
            }
        });
        return animator;
    }

}
