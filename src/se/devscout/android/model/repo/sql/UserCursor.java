package se.devscout.android.model.repo.sql;

import android.database.Cursor;
import se.devscout.android.model.UserBean;

public class UserCursor extends BaseCursorWrapper {
    public UserCursor(Cursor cursor) {
        super(cursor);
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
