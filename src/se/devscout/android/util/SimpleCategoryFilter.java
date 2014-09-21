package se.devscout.android.util;

import android.net.Uri;
import se.devscout.server.api.ActivityFilterVisitor;
import se.devscout.server.api.URIBuilderActivityFilterVisitor;
import se.devscout.server.api.model.ActivityProperties;
import se.devscout.server.api.model.Category;
import se.devscout.server.api.model.CategoryProperties;

/**
 * Tests if activity is assigned to a certain category.
 */
public class SimpleCategoryFilter extends SimpleFilter implements se.devscout.server.api.activityfilter.CategoryFilter {
    private final String name;
    private final String group;
    private int serverId;

    public SimpleCategoryFilter(CategoryProperties properties) {
        name = properties.getName();
        group = properties.getGroup();
        serverId = properties.getServerId();
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean matches(ActivityProperties properties) {
//        ActivityRevision revision = ActivityUtil.getLatestActivityRevision(properties);
        for (Category category : properties.getCategories()) {
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

    @Override
    public String toString(ActivityFilterVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Uri toAPIRequest(URIBuilderActivityFilterVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public int getServerId() {
        return serverId;
    }
}
