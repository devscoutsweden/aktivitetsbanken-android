package se.devscout.android.model.repo.sql;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import se.devscout.android.model.CategoryBean;
import se.devscout.android.model.ObjectIdentifierBean;

import java.util.Set;

public class CategoryCursor extends BaseCursorWrapper {
    public CategoryCursor(SQLiteDatabase db) {
        super(db.query(
                Database.category.T,
                new String[]{Database.category.id, Database.category.server_id, Database.category.server_revision_id, Database.category.group_name, Database.category.name, Database.category.icon_media_id},
                null,
                null,
                null,
                null,
                Database.category.group_name + ", " + Database.category.name));
    }

    public CategoryCursor(SQLiteDatabase db, Set<Long> activityIds) {
        super(db.rawQuery("" +
                "select " +
                "   adc.activity_id activity_id," +
                "   c.* " +
                "from " +
                "   " + Database.category.T + " c inner join " + Database.activity_data_category.T + " adc on c.id = adc.category_id " +
                "where " +
                "   adc.activity_id in (" + TextUtils.join(", ", activityIds) + ")", null));
    }

    public CategoryBean getCategory() {
        return new CategoryBean(
                getString(getColumnIndex(Database.category.group_name)),
                getString(getColumnIndex(Database.category.name)),
                getId(),
                getInt(getColumnIndex(Database.category.server_id)),
                getInt(getColumnIndex(Database.category.server_revision_id)),
                new ObjectIdentifierBean(getLong(getColumnIndex(Database.category.icon_media_id))),
                isNull(getColumnIndex(Database.category.activities_count)) ? null : getInt(getColumnIndex(Database.category.activities_count)));
    }
}
