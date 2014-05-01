package se.devscout.android.util;

import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.ActivityProperties;
import se.devscout.server.api.model.ActivityRevision;
import se.devscout.server.api.model.Category;
import se.devscout.server.api.model.CategoryProperties;

/**
 * Tests if activity is assigned to a certain category.
 */
public class CategoryFilter implements ActivityFilter {
    private final CategoryProperties condition;

    public CategoryFilter(CategoryProperties properties) {
        condition = properties;
    }

    @Override
    public boolean matches(ActivityProperties properties) {
        ActivityRevision revision = ActivityUtil.getLatestActivityRevision(properties);
        for (Category category : revision.getCategories()) {
            if (matches(category)) {
                return true;
            }
        }
        return false;
    }

    private boolean matches(Category candidate) {
        boolean isGroupCorrect = condition.getGroup() == null
                ?
                candidate.getGroup() == null
                :
                condition.getGroup().equals(candidate.getGroup());
        boolean isNameCorrect = condition.getName() == null
                ?
                candidate.getName() == null
                :
                condition.getName().equals(candidate.getName());
        return isNameCorrect && isGroupCorrect;
    }
}
