package se.devscout.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import se.devscout.android.R;

public class ViewPagerIndicator extends View {
    private static final float CRAMPED_DOTS_PADDING_FACTOR = 0.8f;
    private static final float CRAMPED_DOTS_SCALE_FACTOR = 0.95f;
    private static final int CRAMPED_DOTS_MIN_SIZE = 2;
    private static final float CRAMPED_DOTS_INITIAL_SCALE_FACTOR = 1.0f;
    private final int mColor;
    private int mCount = 0;
    private int mSelectedIndex = 0;

    private Paint mPaintFilled;
    private Paint mPaintOutline;
    private float mRegionWidth;
    private float mRegionHeight;
    private float mMaxDiameter;
    private RectF mDrawingArea;
    private float mPaddingDots;
    private DotData[] mDotDatas;

    private static class DotData {
        private DotData(float radiusRatio) {
            this.radiusRatio = radiusRatio;
        }

        float radiusRatio;
        float diameter;
        public double width;
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator, 0, 0);
        try {
            setCount(a.getInt(R.styleable.ViewPagerIndicator_count, 0));
            setSelectedIndex(a.getInt(R.styleable.ViewPagerIndicator_selectedIndex, 0));
            setPaddingDots(a.getDimension(R.styleable.ViewPagerIndicator_paddingDots, 0));
            mColor = a.getColor(R.styleable.ViewPagerIndicator_color, android.R.color.darker_gray);
            mPaintFilled = createPaint(mColor, Paint.Style.FILL);
            mPaintOutline = createPaint(mColor, Paint.Style.STROKE);
        } finally {
            a.recycle();
        }
    }

    private void onPropertyChanged() {
        initDotData();
        invalidate();
        requestLayout();
    }

    private void initDotData() {
        if (mCount > mSelectedIndex && mSelectedIndex >= 0) {
            int count = mCount;
            int countToTheLeft = mSelectedIndex;
            mDotDatas = new DotData[count];
            mDotDatas[countToTheLeft] = new DotData(CRAMPED_DOTS_INITIAL_SCALE_FACTOR);
            for (int i = countToTheLeft - 1; i >= 0; i--) {
                mDotDatas[i] = new DotData(mDotDatas[i + 1].radiusRatio * CRAMPED_DOTS_SCALE_FACTOR);
            }
            for (int i = countToTheLeft + 1; i < count; i++) {
                mDotDatas[i] = new DotData(mDotDatas[i - 1].radiusRatio * CRAMPED_DOTS_SCALE_FACTOR);
            }

            double sumRadius = 0;
            for (int i = 0; i < mDotDatas.length; i++) {
                sumRadius += mDotDatas[i].radiusRatio;
            }

            for (int i = 0; i < mDotDatas.length; i++) {
                double dotWidth = mRegionWidth * (mDotDatas[i].radiusRatio / sumRadius);
                float dotHeight = mRegionHeight * mDotDatas[i].radiusRatio;
                mDotDatas[i].diameter = (float) Math.max(Math.min(dotWidth, dotHeight) * CRAMPED_DOTS_PADDING_FACTOR, CRAMPED_DOTS_MIN_SIZE);
                mDotDatas[i].width = dotWidth;
            }
        } else {
            mDotDatas = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int count = mCount;
        int highestCountPossibleToDraw = (int) (mRegionWidth / (mMaxDiameter + mPaddingDots));

        if (count > highestCountPossibleToDraw) {
            float y = mDrawingArea.centerY();
            float x = mDrawingArea.left;

            if (mDotDatas != null) {
                for (int i = 0; i < mDotDatas.length; i++) {
                    if (mDotDatas[i].width > CRAMPED_DOTS_MIN_SIZE) {
                        drawDot(canvas, y - (mDotDatas[i].diameter * 0.5f), x + (mDotDatas[i].diameter * 0.5f), mDotDatas[i].radiusRatio == 1.0 ? mPaintFilled : mPaintOutline, mDotDatas[i].diameter);
                    }
                    x += mDotDatas[i].width;
                }
            }
        } else {
            float x = (mDrawingArea.width() - (count * mMaxDiameter) - ((count - 1) * mPaddingDots)) / 2;
            float y = mDrawingArea.top;
            for (int i = 0; i < mCount; i++) {
                drawDot(canvas, y, x, i == mSelectedIndex ? mPaintFilled : mPaintOutline);
                x += mMaxDiameter + mPaddingDots;
            }
        }
    }

    private void drawDot(Canvas canvas, float y, float x, Paint paint) {
        drawDot(canvas, y, x, paint, mMaxDiameter);
    }

    private void drawDot(Canvas canvas, float y, float x, Paint paint, float diameter) {
        canvas.drawOval(
                new RectF(
                        x,
                        y,
                        x + diameter,
                        y + diameter),
                paint);
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        if (count < 0) {
            Log.i(ViewPagerIndicator.class.getName(), "Cannot set maximum value to " + count + " since minimum is 0. Will set maximum to 0.");
            count = 0;
        }
        mCount = count;
        onPropertyChanged();
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        if (selectedIndex > mCount) {
            Log.i(ViewPagerIndicator.class.getName(), "Cannot set current value to " + selectedIndex + " since maximum is " + mCount + ". Will set current to " + mCount + ".");
            selectedIndex = mCount;
        } else if (selectedIndex < 0) {
            Log.i(ViewPagerIndicator.class.getName(), "Cannot set current value to " + selectedIndex + " since minimum is 0. Will set current to 0.");
            selectedIndex = 0;
        }
        mSelectedIndex = selectedIndex;
        onPropertyChanged();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mDrawingArea = new RectF(
                getPaddingLeft(),
                getPaddingTop(),
                w - getPaddingRight(),
                h - getPaddingBottom());

        mRegionWidth = mDrawingArea.width();
        mRegionHeight = mDrawingArea.height();

        mMaxDiameter = Math.min(mRegionWidth, mRegionHeight);

        onPropertyChanged();
    }

    private static Paint createPaint(int color, Paint.Style style) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(style);
        return paint;
    }

    public void setPaddingDots(float paddingDots) {
        mPaddingDots = paddingDots;
        onPropertyChanged();
    }

    public float getPaddingDots() {
        return mPaddingDots;
    }
}
