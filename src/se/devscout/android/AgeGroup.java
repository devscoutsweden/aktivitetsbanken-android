package se.devscout.android;

import se.devscout.server.api.model.ObjectIdentifier;
import se.devscout.server.api.model.Range;
import se.devscout.server.api.model.ScoutAgeRange;

public enum AgeGroup implements Range<Integer>, ObjectIdentifier {
    TRACKER(R.string.age_group_TRACKER, ScoutAgeRange.TRACKER, R.drawable.logo_tracker, R.color.green),
    DISCOVERER(R.string.age_group_DISCOVERER, ScoutAgeRange.DISCOVERER, R.drawable.logo_discoverer, R.color.blue),
    ADVENTURER(R.string.age_group_ADVENTURER, ScoutAgeRange.ADVENTURER, R.drawable.logo_adventurer, R.color.orange),
    CHALLENGER(R.string.age_group_CHALLENGER, ScoutAgeRange.CHALLENGER, R.drawable.logo_challenger, R.color.magenta),
    ROVER(R.string.age_group_ROVER, ScoutAgeRange.ROVER, R.drawable.logo_rover, R.color.yellow);

    private final ScoutAgeRange scoutAgeRange;
    private final int logoResId;
    private final int titleResId;
    private final int mColorResId;

    AgeGroup(int titleResId, ScoutAgeRange scoutAgeRange, int logoResId, int colorResId) {
        this.titleResId = titleResId;
        this.scoutAgeRange = scoutAgeRange;
        this.logoResId = logoResId;
        mColorResId = colorResId;
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

    public int getColorResId() {
        return mColorResId;
    }

    @Override
    public Long getId() {
        return Long.valueOf(ordinal());
    }
}
