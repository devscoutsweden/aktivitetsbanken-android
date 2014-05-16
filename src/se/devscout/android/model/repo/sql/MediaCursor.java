package se.devscout.android.model.repo.sql;

import android.database.Cursor;
import se.devscout.android.model.repo.LocalMedia;

import java.net.URI;
import java.net.URISyntaxException;

public class MediaCursor extends BaseCursorWrapper {
    public MediaCursor(Cursor cursor) {
        super(cursor);
    }

    public LocalMedia getMedia() throws URISyntaxException {
            return new LocalMedia(
                    new URI(getString(getColumnIndex(Database.media.uri))),
                    getString(getColumnIndex(Database.media.mime_type)),
                    getId());
    }
}
