package se.devscout.android;

import android.graphics.Color;
import se.devscout.server.api.model.ObjectIdentifier;
import se.devscout.server.api.model.Range;
import se.devscout.server.api.model.ScoutAgeRange;

public enum AgeGroup implements Range<Integer>, ObjectIdentifier {
    TRACKER(R.string.age_group_TRACKER, ScoutAgeRange.TRACKER, R.drawable.logo_tracker, Color.rgb(91, 172, 38)),
    DISCOVERER(R.string.age_group_DISCOVERER, ScoutAgeRange.DISCOVERER, R.drawable.logo_discoverer, Color.rgb(0, 156, 218)),
    ADVENTURER(R.string.age_group_ADVENTURER, ScoutAgeRange.ADVENTURER, R.drawable.logo_adventurer, Color.rgb(237, 119, 3)),
    CHALLENGER(R.string.age_group_CHALLENGER, ScoutAgeRange.CHALLENGER, R.drawable.logo_challenger, Color.rgb(220, 0, 107)),
    ROVER(R.string.age_group_ROVER, ScoutAgeRange.ROVER, R.drawable.logo_rover, Color.rgb(241, 228, 0));

    private final ScoutAgeRange scoutAgeRange;
    private final int logoResId;
    private final int titleResId;
    private final int mColor;

    AgeGroup(int titleResId, ScoutAgeRange scoutAgeRange, int logoResId, int color) {
        this.titleResId = titleResId;
        this.scoutAgeRange = scoutAgeRange;
        this.logoResId = logoResId;
        mColor = color;
    }

    public int getLogoResId() {
        return logoResId;
    }

    public ScoutAgeRange getScoutAgeRange() {
        return scoutAgeRange;
    }

    @Override
    public Integer getMax() {
        return scoutAgeRange.getMax();
    }

    @Override
    public Integer getMin() {
        return scoutAgeRange.getMin();
    }

    public static AgeGroup valueOf(ScoutAgeRange scoutAgeRange) {
        for (AgeGroup ageGroup : values()) {
            if (ageGroup.scoutAgeRange == scoutAgeRange) {
                return ageGroup;
            }
        }
        return null;
    }

    public int getTitleResId() {
        return titleResId;
    }

    public int getColor() {
        return mColor;
    }

    @Override
    public Long getId() {
        return Long.valueOf(ordinal());
    }
}
