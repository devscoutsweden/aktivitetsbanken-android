package se.devscout.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import se.devscout.android.AgeGroup;
import se.devscout.android.R;
import se.devscout.android.model.IntegerRange;
import se.devscout.android.util.SimpleAgeRangeFilter;

public class AgeGroupView extends View {
    private static final float INACTIVE_SIZE_RATIO = 0.25f;
    private final int mInactiveColor;
    private int mMinAge = 13;
    private int mMaxAge = 16;
//    private int mSizeWidth;
//    private int mSizeHeight;

    private Paint[] mMatchingAgeGroupPainters = new Paint[AgeGroup.values().length];
    private Paint mInactivePaint;
    private float mRegionWidth;
    private float mRegionHeight;
    private float mMaxDiameter;
    private RectF mDrawingArea;

    public AgeGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AgeGroupView, 0, 0);
        try {
            setMinAge(a.getInt(R.styleable.AgeGroupView_minAge, 0));
            setMaxAge(a.getInt(R.styleable.AgeGroupView_maxAge, Integer.MAX_VALUE));
            mInactiveColor = a.getColor(R.styleable.AgeGroupView_inactivePaint, android.R.color.darker_gray);
            mInactivePaint = createPaint(mInactiveColor);
        } finally {
            a.recycle();
        }
    }

    private void onAgeRangeChanged() {
        for (int i = 0; i < AgeGroup.values().length; i++) {
            AgeGroup ageGroup = AgeGroup.values()[i];
            SimpleAgeRangeFilter simpleAgeRangeFilter = new SimpleAgeRangeFilter(ageGroup.getScoutAgeRange());
            mMatchingAgeGroupPainters[i] = (simpleAgeRangeFilter.isPartlyWithin(new IntegerRange(mMinAge, mMaxAge)) ? createPaint(getResources().getColor(ageGroup.getColorResId())) : null);
        }
        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < mMatchingAgeGroupPainters.length; i++) {
            Paint paint = mMatchingAgeGroupPainters[i];

            boolean isActive = paint != null;
            float diameter = isActive ? mMaxDiameter : mMaxDiameter * INACTIVE_SIZE_RATIO;
            float regionPaddingX = (mRegionWidth - diameter) / 2;
            float regionPaddingY = (mRegionHeight - diameter) / 2;

            float left = mDrawingArea.left + (i * mRegionWidth) + regionPaddingX;
            float top = mDrawingArea.top + regionPaddingY;
            canvas.drawOval(
                    new RectF(
                            left,
                            top,
                            left + diameter,
                            top + diameter),
                    isActive ? paint : mInactivePaint);
        }
    }

    public int getMaxAge() {
        return mMaxAge;
    }

    public void setMaxAge(int maxAge) {
        mMaxAge = maxAge;
        onAgeRangeChanged();
    }

    public int getMinAge() {
        return mMinAge;
    }

    public void setMinAge(int minAge) {
        mMinAge = minAge;
        onAgeRangeChanged();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        mSizeWidth = w;
//        mSizeHeight = h;
        mDrawingArea = new RectF(
                getPaddingLeft(),
                getPaddingTop(),
                w - getPaddingRight(),
                h - getPaddingBottom());

        mRegionWidth = mDrawingArea.width() / mMatchingAgeGroupPainters.length;
        mRegionHeight = mDrawingArea.height();

        mMaxDiameter = Math.min(mRegionWidth, mRegionHeight);// * 0.8f;
    }

    private static Paint createPaint(int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        return paint;
    }
}
