package se.devscout.android.controller.fragment;

import se.devscout.android.model.CategoryPropertiesBean;
import se.devscout.server.api.model.Category;

public class CategoryListItem extends CategoryPropertiesBean {
    public CategoryListItem(Category category) {
        super(category.getGroup(), category.getName(), category.getServerId(), category.getServerRevisionId(), false, category.getIconMediaKey(), category.getActivitiesCount());
    }
}
