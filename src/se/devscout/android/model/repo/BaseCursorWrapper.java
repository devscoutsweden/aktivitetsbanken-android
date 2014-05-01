package se.devscout.android.model.repo;

import android.database.Cursor;
import android.database.CursorWrapper;

public class BaseCursorWrapper extends CursorWrapper {
    public BaseCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public long getId() {
        return getLong(getColumnIndex("id"));
    }
}
