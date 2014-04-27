package se.devscout.android.model.repo;

import se.devscout.server.api.model.Range;

import java.io.Serializable;

public class LocalRange implements Range<Integer>, Serializable {

    private Integer mMin = Integer.MIN_VALUE;
    private Integer mMax = Integer.MAX_VALUE;

    public LocalRange() {
    }

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
