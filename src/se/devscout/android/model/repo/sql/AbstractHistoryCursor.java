package se.devscout.android.model.repo.sql;

import android.database.sqlite.SQLiteDatabase;
import se.devscout.android.model.ObjectIdentifierBean;
import se.devscout.server.api.model.HistoryType;
import se.devscout.server.api.model.UserKey;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public abstract class AbstractHistoryCursor<P, T extends Serializable> extends BaseCursorWrapper {
    public AbstractHistoryCursor(SQLiteDatabase db, UserKey user, boolean descendingOrder, HistoryType type) {
        super(db.query(Database.history.T,
                new String[]{
                        Database.history.id,
                        Database.history.user_id,
                        Database.history.type,
                        Database.history.data
                },
                Database.history.user_id + " = " + user.getId() + " and " + Database.history.type + " = ?",
                new String[]{String.valueOf(type.getDatabaseValue())},
                null,
                null,
                Database.history.id + (descendingOrder ? " DESC" : "")));
    }


    public P getHistoryItem() throws IOException, ClassNotFoundException {

        byte[] bytes = getBlob(getColumnIndex(Database.history.data));
        ByteArrayInputStream baos = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(baos);
        Object data = in.readObject();
        baos.close();
        in.close();

        P item = createHistoryItem(
                getId(),
                new ObjectIdentifierBean(getLong(getColumnIndex(Database.history.user_id))),
                (T) data);

        return item;
    }

    protected abstract P createHistoryItem(long id, UserKey userKey, T data);
}
