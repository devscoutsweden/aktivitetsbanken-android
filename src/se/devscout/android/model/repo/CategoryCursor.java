package se.devscout.android.model.repo;

import android.database.Cursor;

public class CategoryCursor extends BaseCursorWrapper {
    public CategoryCursor(Cursor cursor) {
        super(cursor);
    }

    public LocalCategory getCategory() {
        return new LocalCategory(
                getString(getColumnIndex(Database.category.group_name)),
                getString(getColumnIndex(Database.category.name)),
                getId());
    }
}
