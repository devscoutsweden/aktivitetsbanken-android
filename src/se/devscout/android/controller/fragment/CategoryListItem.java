package se.devscout.android.controller.fragment;

import se.devscout.android.model.Category;
import se.devscout.android.model.CategoryPropertiesBean;

public class CategoryListItem extends CategoryPropertiesBean {
    public CategoryListItem(Category category) {
        super(category.getGroup(), category.getName(), category.getServerId(), category.getServerRevisionId(), false, category.getIconMediaKey(), category.getActivitiesCount());
    }
}
