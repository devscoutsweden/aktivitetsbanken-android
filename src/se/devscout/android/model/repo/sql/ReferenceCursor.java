package se.devscout.android.model.repo.sql;

import android.database.Cursor;
import se.devscout.android.model.repo.ReferenceBean;
import se.devscout.server.api.model.ReferenceType;

import java.net.URI;
import java.net.URISyntaxException;

public class ReferenceCursor extends BaseCursorWrapper {
    public ReferenceCursor(Cursor cursor) {
        super(cursor);
    }


    public ReferenceBean getReference() throws URISyntaxException {
        return new ReferenceBean(
                getId(),
                getInt(getColumnIndex(Database.reference.server_id)),
                getInt(getColumnIndex(Database.reference.server_revision_id)),
                ReferenceType.READ_MORE,
                new URI(getString(getColumnIndex(Database.reference.uri))));

    }
}
