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
    private final int mColor;
    private int mMin = 0;
    private int mMax = 0;
    private int mCurrent = 0;

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
            setMin(a.getInt(R.styleable.ViewPagerIndicator_min, 0));
            setMax(a.getInt(R.styleable.ViewPagerIndicator_max, 0));
            setCurrent(a.getInt(R.styleable.ViewPagerIndicator_current, 0));
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
        int count = mMax - mMin + 1;
        int countToTheLeft = mCurrent - mMin;
        mDotDatas = new DotData[count];
        mDotDatas[countToTheLeft] = new DotData(1.0f);
        for (int i = countToTheLeft - 1; i >= 0; i--) {
            mDotDatas[i] = new DotData(mDotDatas[i + 1].radiusRatio * 0.95f);
        }
        for (int i = countToTheLeft + 1; i < count; i++) {
            mDotDatas[i] = new DotData(mDotDatas[i - 1].radiusRatio * 0.95f);
        }

        double sumRadius = 0;
        for (int i = 0; i < mDotDatas.length; i++) {
            sumRadius += mDotDatas[i].radiusRatio;
        }

        for (int i = 0; i < mDotDatas.length; i++) {
            double dotWidth = mRegionWidth * (mDotDatas[i].radiusRatio / sumRadius);
            float dotHeight = mRegionHeight * mDotDatas[i].radiusRatio;
            mDotDatas[i].diameter = (float) Math.min(dotWidth, dotHeight);
            mDotDatas[i].width = dotWidth;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int count = mMax - mMin + 1;
        int highestCountPossibleToDraw = (int) (mRegionWidth / (mMaxDiameter + mPaddingDots));

        if (count > highestCountPossibleToDraw) {
            float y = mDrawingArea.centerY();
            float x = mDrawingArea.left;

            for (int i = 0; i < mDotDatas.length; i++) {
                drawDot(canvas, y - (mDotDatas[i].diameter * 0.5f), x + (mDotDatas[i].diameter * 0.5f), mDotDatas[i].radiusRatio == 1.0 ? mPaintFilled : mPaintOutline, mDotDatas[i].diameter);
                x += mDotDatas[i].width;
            }
        } else {
            float x = (mDrawingArea.width() - (count * mMaxDiameter) - ((count - 1) * mPaddingDots)) / 2;
            float y = mDrawingArea.top;
            for (int i = mMin; i <= mMax; i++) {
                drawDot(canvas, y, x, i == mCurrent ? mPaintFilled : mPaintOutline);
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

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        if (max < mMin) {
            Log.i(ViewPagerIndicator.class.getName(), "Cannot set maximum value to " + max + " since minimum is " + mMin + ". Will set maximum to " + mMin + ".");
            max = mMin;
        }
        mMax = max;
        onPropertyChanged();
    }

    public int getCurrent() {
        return mCurrent;
    }

    public void setCurrent(int current) {
        if (current > mMax) {
            Log.i(ViewPagerIndicator.class.getName(), "Cannot set current value to " + current + " since maximum is " + mMax + ". Will set current to " + mMax + ".");
            current = mMax;
        } else if (current < mMin) {
            Log.i(ViewPagerIndicator.class.getName(), "Cannot set current value to " + current + " since minimum is " + mMin + ". Will set current to " + mMin + ".");
            current = mMin;
        }
        mCurrent = current;
        onPropertyChanged();
    }

    public int getMin() {
        return mMin;
    }

    public void setMin(int min) {
        if (min > mMax) {
            Log.i(ViewPagerIndicator.class.getName(), "Cannot set minimum value to " + min + " since maximum is " + mMax + ". Will set minimum to " + mMax + ".");
            min = mMax;
        }
        mMin = min;
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
