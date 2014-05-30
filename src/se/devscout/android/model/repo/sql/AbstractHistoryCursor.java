package se.devscout.android.model.repo.sql;

import android.database.Cursor;
import se.devscout.android.model.ObjectIdentifierPojo;
import se.devscout.android.model.repo.LocalSearchHistory;
import se.devscout.server.api.model.UserKey;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public abstract class AbstractHistoryCursor<P extends LocalSearchHistory, T extends Serializable> extends BaseCursorWrapper {
    public AbstractHistoryCursor(Cursor cursor) {
        super(cursor);
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
                new ObjectIdentifierPojo(getLong(getColumnIndex(Database.history.user_id))),
                (T) data);

        return item;
    }

    protected abstract P createHistoryItem(long id, UserKey userKey, T data);
}
