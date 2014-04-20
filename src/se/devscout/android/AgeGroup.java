package se.devscout.android;

import se.devscout.server.api.model.Range;
import se.devscout.server.api.model.ScoutAgeRange;

public enum AgeGroup implements Range<Integer> {
    TRACKER(R.string.age_group_TRACKER, ScoutAgeRange.TRACKER, R.drawable.logo_tracker),
    DISCOVERER(R.string.age_group_DISCOVERER, ScoutAgeRange.DISCOVERER, R.drawable.logo_discoverer),
    ADVENTURER(R.string.age_group_ADVENTURER, ScoutAgeRange.ADVENTURER, R.drawable.logo_adventurer),
    CHALLENGER(R.string.age_group_CHALLENGER, ScoutAgeRange.CHALLENGER, R.drawable.logo_challenger),
    ROVER(R.string.age_group_ROVER, ScoutAgeRange.ROVER, R.drawable.logo_rover);

    private final ScoutAgeRange scoutAgeRange;
    private final int logoResId;
    private final int titleResId;

    AgeGroup(int titleResId, ScoutAgeRange scoutAgeRange, int logoResId) {
        this.titleResId = titleResId;
        this.scoutAgeRange = scoutAgeRange;
        this.logoResId = logoResId;
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

    public String getName() {
        return scoutAgeRange.getName();
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
}
