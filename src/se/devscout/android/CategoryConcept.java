package se.devscout.android;

import se.devscout.server.api.model.ScoutCategoryConcept;

public enum CategoryConcept {
    SCOUTSOWN(R.string.category_concept_SCOUTSOWN, ScoutCategoryConcept.SCOUTSOWN, -1),
    CEREMONY(R.string.category_concept_CEREMONY, ScoutCategoryConcept.CEREMONY, -1),
    DISCUSSION(R.string.category_concept_DISCUSSION, ScoutCategoryConcept.DISCUSSION, -1),
    HIKE(R.string.category_concept_HIKE, ScoutCategoryConcept.HIKE, -1),
    HANDICRAFT(R.string.category_concept_HANDICRAFT, ScoutCategoryConcept.HANDICRAFT, -1),
    CAMPFIRE(R.string.category_concept_CAMPFIRE, ScoutCategoryConcept.CAMPFIRE, -1),
    PLAYTIME(R.string.category_concept_PLAYTIME, ScoutCategoryConcept.PLAYTIME, -1),
    COOK(R.string.category_concept_COOK, ScoutCategoryConcept.COOK, -1),
    OUTDOOR_INLAND(R.string.category_concept_OUTDOOR_INLAND, ScoutCategoryConcept.OUTDOOR_INLAND, -1),
    OUTDOOR_LAKE(R.string.category_concept_OUTDOOR_LAKE, ScoutCategoryConcept.OUTDOOR_LAKE, -1),
    ART(R.string.category_concept_ART, ScoutCategoryConcept.ART, -1),
    OUTDOOR(R.string.category_concept_OUTDOOR, ScoutCategoryConcept.OUTDOOR, -1);

    private final ScoutCategoryConcept scoutAgeRange;
    private final int logoResId;
    private final int titleResId;

    CategoryConcept(int titleResId, ScoutCategoryConcept scoutAgeRange, int logoResId) {
        this.titleResId = titleResId;
        this.scoutAgeRange = scoutAgeRange;
        this.logoResId = logoResId;
    }

    public int getLogoResId() {
        return logoResId;
    }

    public ScoutCategoryConcept getScoutCategoryConcept() {
        return scoutAgeRange;
    }

    public String getName() {
        return scoutAgeRange.getName();
    }

    public static CategoryConcept valueOf(ScoutCategoryConcept scoutCategoryConcept) {
        for (CategoryConcept concept : values()) {
            if (concept.scoutAgeRange == scoutCategoryConcept) {
                return concept;
            }
        }
        return null;
    }

    public int getTitleResId() {
        return titleResId;
    }
}
