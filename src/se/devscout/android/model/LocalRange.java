package se.devscout.android.model;

import se.devscout.shared.data.model.Range;

import java.io.Serializable;

public class LocalRange implements Range<Integer>, Serializable {

    private Integer mMin = Integer.MIN_VALUE;
    private Integer mMax = Integer.MAX_VALUE;

    public LocalRange(Integer min, Integer max) {
        mMax = max;
        mMin = min;
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
        return mMin + "-" + mMax;
    }
}
