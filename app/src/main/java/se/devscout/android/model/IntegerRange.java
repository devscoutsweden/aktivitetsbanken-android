package se.devscout.android.model;

import java.io.Serializable;

public class IntegerRange implements Range<Integer>, Serializable {

    private Integer mMin = Integer.MIN_VALUE;
    private Integer mMax = Integer.MAX_VALUE;

    public IntegerRange() {
    }

    public IntegerRange(int min, int max) {
        if (min > 0) {
            mMin = min;
        }
        if (max > 0) {
            mMax = max;
        }
    }

    @Override
    public Integer getMin() {
        return mMin;
    }

    @Override
    public Integer getMax() {
        return mMax;
    }

    @Override
    public String toString() {
        int min = Math.max(0, mMin);
        int max = Math.min(99, mMax);
        if (min > 0 && max < 99) {
            return min + "-" + max;
        } else if (max == 99) {
            return "1-99+";
        } else {
            return "";
        }
    }

    public void setMax(Integer max) {
        mMax = max;
    }

    public void setMin(Integer min) {
        mMin = min;
    }
}
