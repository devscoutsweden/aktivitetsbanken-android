package se.devscout.android.model.repo.sql;

import android.database.Cursor;
import se.devscout.android.model.repo.LocalCategory;

public class CategoryCursor extends BaseCursorWrapper {
    public CategoryCursor(Cursor cursor) {
        super(cursor);
    }

    public LocalCategory getCategory() {
        return new LocalCategory(
                getString(getColumnIndex(Database.category.group_name)),
                getString(getColumnIndex(Database.category.name)),
                getId(),
                getInt(getColumnIndex(Database.category.server_id)));
    }
}
