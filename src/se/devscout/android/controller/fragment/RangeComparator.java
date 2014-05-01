package se.devscout.android.controller.fragment;

import se.devscout.server.api.model.Activity;
import se.devscout.server.api.model.Range;

import java.util.Comparator;

public abstract class RangeComparator implements Comparator<Activity> {
    @Override
    public int compare(Activity localActivity, Activity localActivity2) {
        Range<Integer> participants = getRange(localActivity);
        Range<Integer> participants2 = getRange(localActivity2);
        return compareRange(participants, participants2);
    }

    protected abstract Range<Integer> getRange(Activity activity);

    private int compareRange(Range<Integer> range, Range<Integer> range2) {
        if (range != null && range2 != null) {
            // Sort by value in middle of range
            int rangeMiddle = range.getMin() + range.getMax() / 2;
            int range2Middle = range2.getMin() + range2.getMax() / 2;
            return rangeMiddle - range2Middle;
        } else if (range != null) {
            return -1;
        } else if (range2 != null) {
            return 1;
        } else {
            return 0;
        }
    }
}
