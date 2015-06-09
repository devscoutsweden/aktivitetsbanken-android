package se.devscout.android.model.repo.sql;

import android.database.sqlite.SQLiteDatabase;
import se.devscout.android.model.SystemMessageBean;

import java.net.URISyntaxException;
import java.util.Date;

public class SystemMessageCursor extends BaseCursorWrapper {
    public SystemMessageCursor(SQLiteDatabase db) {
        super(db.query(
                Database.system_messages.T,
                new String[]{
                        Database.system_messages.id,
                        Database.system_messages.server_id,
                        Database.system_messages.server_revision_id,
                        Database.system_messages.valid_from,
                        Database.system_messages.valid_to,
                        Database.system_messages.key,
                        Database.system_messages.value
                },
                null,
                null,
                null,
                null,
                null));
    }

    public SystemMessageBean getReference() throws URISyntaxException {
        return new SystemMessageBean(
                getId(),
                getInt(getColumnIndex(Database.system_messages.server_id)),
                getInt(getColumnIndex(Database.system_messages.server_revision_id)),
                getString(getColumnIndex(Database.system_messages.key)),
                !isNull(getColumnIndex(Database.system_messages.valid_from)) ? new Date(getLong(getColumnIndex(Database.system_messages.valid_from))) : null,
                !isNull(getColumnIndex(Database.system_messages.valid_to)) ? new Date(getLong(getColumnIndex(Database.system_messages.valid_to))) : null,
                getString(getColumnIndex(Database.system_messages.value)));
    }
}
