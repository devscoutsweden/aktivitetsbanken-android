package se.devscout.android.model.repo.sql;

import android.database.Cursor;
import se.devscout.android.model.repo.MediaBean;

import java.net.URI;
import java.net.URISyntaxException;

public class MediaCursor extends BaseCursorWrapper {
    public MediaCursor(Cursor cursor) {
        super(cursor);
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
