package se.devscout.android.model.repo.sql;

import android.database.Cursor;
import se.devscout.android.model.repo.CategoryBean;

public class CategoryCursor extends BaseCursorWrapper {
    public CategoryCursor(Cursor cursor) {
        super(cursor);
    }

    public CategoryBean getCategory() {
        return new CategoryBean(
                getString(getColumnIndex(Database.category.group_name)),
                getString(getColumnIndex(Database.category.name)),
                getId(),
                getInt(getColumnIndex(Database.category.server_id)),
                getInt(getColumnIndex(Database.category.server_revision_id)));
    }
}
