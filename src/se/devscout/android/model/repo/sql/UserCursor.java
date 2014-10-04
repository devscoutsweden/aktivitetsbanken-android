package se.devscout.android.model.repo.sql;

import android.database.sqlite.SQLiteDatabase;
import se.devscout.android.model.UserBean;

public class UserCursor extends BaseCursorWrapper {
    public UserCursor(SQLiteDatabase db) {
        super(db.query(
                Database.user.T,
                new String[]{Database.user.id, Database.user.server_id, Database.user.server_revision_id, Database.user.display_name, Database.user.email, Database.user.email_verified, Database.user.password_algorithm, Database.user.password_hash},
                null,
                null,
                null,
                null,
                null));
    }

    public UserBean getUser() {
        return new UserBean(
                getString(getColumnIndex(Database.user.display_name)),
                getString(getColumnIndex(Database.user.email)),
                getId(),
                getInt(getColumnIndex(Database.user.server_id)),
                getInt(getColumnIndex(Database.user.server_revision_id))
        );
    }
}
