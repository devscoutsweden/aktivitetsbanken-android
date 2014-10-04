package se.devscout.android.model.repo.sql;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import se.devscout.android.model.ReferenceBean;
import se.devscout.server.api.model.ReferenceType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

public class ReferenceCursor extends BaseCursorWrapper {
    public ReferenceCursor(SQLiteDatabase db) {
        super(db.query(
                Database.reference.T,
                new String[]{Database.reference.id, Database.reference.server_id, Database.reference.server_revision_id, Database.reference.type, Database.reference.uri},
                null,
                null,
                null,
                null,
                null));
    }

    public ReferenceCursor(SQLiteDatabase db, Set<Long> activityIds) {
        super(db.rawQuery("" +
                "select " +
                "   adr.activity_id activity_id," +
                "   r.* " +
                "from " +
                "   " + Database.reference.T + " r inner join " + Database.activity_data_reference.T + " adr on r.id = adr.reference_id " +
                "where " +
                "   adr.activity_id in (" + TextUtils.join(", ", activityIds) + ")", null));
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
