package se.devscout.android.model.repo.sql;

import android.database.sqlite.SQLiteDatabase;
import se.devscout.android.model.UserBean;

public class UserCursor extends BaseCursorWrapper {
    public UserCursor(SQLiteDatabase db) {
        super(db.query(
                Database.user.T,
                new String[]{Database.user.id, Database.user.server_id, Database.user.server_revision_id, Database.user.display_name, Database.user.api_key, Database.user.email_address, Database.user.role},
                null,
                null,
                null,
                null,
                null));
    }

    public UserBean getUser() {
        return new UserBean(
                getString(getColumnIndex(Database.user.display_name)),
                getString(getColumnIndex(Database.user.api_key)),
                getId(),
                getInt(getColumnIndex(Database.user.server_id)),
                getInt(getColumnIndex(Database.user.server_revision_id)),
                getString(getColumnIndex(Database.user.email_address)),
                getString(getColumnIndex(Database.user.role))
                );
    }
}
