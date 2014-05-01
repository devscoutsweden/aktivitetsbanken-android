package se.devscout.android.model;

import se.devscout.server.api.model.Range;

import java.io.Serializable;

public class IntegerRangePojo implements Range<Integer>, Serializable {

    private Integer mMin = Integer.MIN_VALUE;
    private Integer mMax = Integer.MAX_VALUE;

    public IntegerRangePojo() {
    }

    public IntegerRangePojo(int min, int max) {
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
        if (mMin != Integer.MIN_VALUE && mMax != Integer.MAX_VALUE) {
            return mMin + "-" + mMax;
        } else if (mMin != Integer.MIN_VALUE) {
            return ">=" + mMin;
        } else if (mMax != Integer.MAX_VALUE) {
            return "<=" + mMax;
        } else {
            return "";
        }
    }
}
