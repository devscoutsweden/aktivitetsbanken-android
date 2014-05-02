package se.devscout.android.util;

import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.ActivityProperties;
import se.devscout.server.api.model.ActivityRevision;
import se.devscout.server.api.model.Category;
import se.devscout.server.api.model.CategoryProperties;

import java.io.Serializable;

/**
 * Tests if activity is assigned to a certain category.
 */
public class CategoryFilter implements ActivityFilter, Serializable {
    private final String name;
    private final String group;

    public CategoryFilter(CategoryProperties properties) {
        name = properties.getName();
        group = properties.getGroup();
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
        boolean isGroupCorrect = group == null
                ?
                candidate.getGroup() == null
                :
                group.equals(candidate.getGroup());
        boolean isNameCorrect = name == null
                ?
                candidate.getName() == null
                :
                name.equals(candidate.getName());
        return isNameCorrect && isGroupCorrect;
    }
}
