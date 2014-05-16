package se.devscout.android.model.repo.sql;

import android.database.Cursor;
import se.devscout.android.model.repo.LocalReference;
import se.devscout.server.api.model.ReferenceType;

import java.net.URI;
import java.net.URISyntaxException;

public class ReferenceCursor extends BaseCursorWrapper {
    public ReferenceCursor(Cursor cursor) {
        super(cursor);
    }


    public LocalReference getReference() throws URISyntaxException {
        return new LocalReference(
                getId(),
                ReferenceType.READ_MORE/*refCursor.getString(refCursor.getColumnIndex("mime_type"))*/,
                new URI(getString(getColumnIndex(Database.reference.uri))));

    }
}
