package se.devscout.android.model.repo.sql;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import se.devscout.android.model.MediaBean;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

public class MediaCursor extends BaseCursorWrapper {
    public MediaCursor(SQLiteDatabase db) {
        super(db.query(
                Database.media.T,
                new String[]{Database.media.id, Database.media.server_id, Database.media.server_revision_id, Database.media.is_publishable, Database.media.uri, Database.media.mime_type},
                null,
                null,
                null,
                null,
                null));
    }
    public MediaCursor(SQLiteDatabase db, Set<Long> activityIds) {
        super(db.rawQuery("" +
                "select " +
                "   adm.activity_id activity_id," +
                "   m.* " +
                "from " +
                "   " + Database.media.T + " m inner join " + Database.activity_data_media.T + " adm on m.id = adm.media_id " +
                "where " +
                "   adm.activity_id in (" + TextUtils.join(", ", activityIds) + ")", null));
    }

    public MediaBean getMedia() throws URISyntaxException {
        return new MediaBean(
                new URI(getString(getColumnIndex(Database.media.uri))),
                getString(getColumnIndex(Database.media.mime_type)),
                getId(),
                getInt(getColumnIndex(Database.media.server_id)),
                getInt(getColumnIndex(Database.media.server_revision_id)),
                getInt(getColumnIndex(Database.media.is_publishable)) != 0);
    }
}
