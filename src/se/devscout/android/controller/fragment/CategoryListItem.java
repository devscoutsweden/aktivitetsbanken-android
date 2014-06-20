package se.devscout.android.controller.fragment;

import se.devscout.android.model.CategoryPropertiesPojo;
import se.devscout.server.api.model.Category;

public class CategoryListItem extends CategoryPropertiesPojo {
    public CategoryListItem(String group, String name) {
        super(group, name);
    }

    public CategoryListItem(Category category) {
        super(category.getGroup(), category.getName());
    }
}
