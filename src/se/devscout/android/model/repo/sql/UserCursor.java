package se.devscout.android.model.repo.sql;

import android.database.Cursor;
import se.devscout.android.model.repo.LocalUser;

public class UserCursor extends BaseCursorWrapper {
    public UserCursor(Cursor cursor) {
        super(cursor);
    }

    public LocalUser getUser() {
        return new LocalUser(
                getString(getColumnIndex(Database.user.display_name)),
                getInt(getColumnIndex(Database.user.is_local_only)) == 1,
                getString(getColumnIndex(Database.user.email)),
                getId());
    }
}
