package se.devscout.android.util;

import se.devscout.server.api.activityfilter.BaseActivityFilterVisitor;
import se.devscout.server.api.activityfilter.CategoryFilter;
import se.devscout.server.api.model.Category;

/**
 * Tests if activity is assigned to a certain category.
 */
public class SimpleCategoryFilter implements CategoryFilter {
    private final String name;
    private final String group;
    private final long serverId;

    public SimpleCategoryFilter(String group, String name, long serverId) {
        if (serverId <= 0) {
            throw new IllegalArgumentException("Cannot create filter for category {group=" + group + ", name=" + name + "} when serverId=" + serverId);
        }
        this.group = group;
        this.name = name;
        this.serverId = serverId;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getName() {
        return name;
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
    public <T> T visit(BaseActivityFilterVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public long getServerId() {
        return serverId;
    }
}
