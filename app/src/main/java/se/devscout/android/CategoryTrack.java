package se.devscout.android;

import se.devscout.android.model.ObjectIdentifier;
import se.devscout.android.model.ScoutCategoryTrack;

public enum CategoryTrack implements ObjectIdentifier {

    ACTIVE_GROUP(R.string.category_track_ACTIVE_GROUP, ScoutCategoryTrack.ACTIVE_GROUP, -1),
    ACTIVE_COMMUNITY(R.string.category_track_ACTIVE_COMMUNITY, ScoutCategoryTrack.ACTIVE_COMMUNITY, -1),
    PERSONAL_VIEWS(R.string.category_track_PERSONAL_VIEWS, ScoutCategoryTrack.PERSONAL_VIEWS, -1),
    EXISTENCE(R.string.category_track_EXISTENCE, ScoutCategoryTrack.EXISTENCE, -1),
    CREATIVE_EXPRESSION(R.string.category_track_CREATIVE_EXPRESSION, ScoutCategoryTrack.CREATIVE_EXPRESSION, -1),
    PHYSICAL_CHALLENGES(R.string.category_track_PHYSICAL_CHALLENGES, ScoutCategoryTrack.PHYSICAL_CHALLENGES, -1),
    UNDERSTANDING_SOCIETY(R.string.category_track_UNDERSTANDING_SOCIETY, ScoutCategoryTrack.UNDERSTANDING_SOCIETY, -1),
    ANALYTIC_THINKING(R.string.category_track_ANALYTIC_THINKING, ScoutCategoryTrack.ANALYTIC_THINKING, -1),
    NATURE(R.string.category_track_NATURE, ScoutCategoryTrack.NATURE, -1),
    LEADERSHIP(R.string.category_track_LEADERSHIP, ScoutCategoryTrack.LEADERSHIP, -1),
    PROBLEM_SOLVING(R.string.category_track_PROBLEM_SOLVING, ScoutCategoryTrack.PROBLEM_SOLVING, -1),
    RELATIONS(R.string.category_track_RELATIONS, ScoutCategoryTrack.RELATIONS, -1),
    SELF_ESTEEM(R.string.category_track_SELF_ESTEEM, ScoutCategoryTrack.SELF_ESTEEM, -1),
    PHYSICAL_HEALTH(R.string.category_track_PHYSICAL_HEALTH, ScoutCategoryTrack.PHYSICAL_HEALTH, -1);

    private final ScoutCategoryTrack mScoutCategoryTrack;
    private final int mLogoResId;
    private final int mTitleResId;

    CategoryTrack(int titleResId, ScoutCategoryTrack scoutCategoryTrack, int logoResId) {
        mTitleResId = titleResId;
        mScoutCategoryTrack = scoutCategoryTrack;
        mLogoResId = logoResId;
    }

    public int getLogoResId() {
        return mLogoResId;
    }

    public ScoutCategoryTrack getScoutCategoryTrack() {
        return mScoutCategoryTrack;
    }

    public String getName() {
        return mScoutCategoryTrack.getName();
    }

    public static CategoryTrack valueOf(ScoutCategoryTrack scoutCategoryConcept) {
        for (CategoryTrack concept : values()) {
            if (concept.mScoutCategoryTrack == scoutCategoryConcept) {
                return concept;
            }
        }
        return null;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    @Override
    public Long getId() {
        return Long.valueOf(ordinal());
    }
}
