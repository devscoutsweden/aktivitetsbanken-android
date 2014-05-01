package se.devscout.android.model.repo;

import android.database.Cursor;
import se.devscout.android.model.IntegerRangePojo;
import se.devscout.server.api.model.ActivityKey;

import java.net.URI;
import java.net.URISyntaxException;

public class ActivityDataCursor extends BaseCursorWrapper {
    public ActivityDataCursor(Cursor cursor) {
        super(cursor);
    }

    public LocalActivityRevision getActivityData(ActivityKey activityKey) {
        LocalActivityRevision revision = new LocalActivityRevision(
                getString(getColumnIndex(Database.activity_data.name)),
                getInt(getColumnIndex(Database.activity_data.featured)) == 1,
                activityKey,
                getId()
        );
        revision.setAges(new IntegerRangePojo(getInt(getColumnIndex(Database.activity_data.age_min)), getInt(getColumnIndex(Database.activity_data.age_max))));
        revision.setTimeActivity(new IntegerRangePojo(getInt(getColumnIndex(Database.activity_data.time_min)), getInt(getColumnIndex(Database.activity_data.time_max))));
        revision.setParticipants(new IntegerRangePojo(getInt(getColumnIndex(Database.activity_data.participants_min)), getInt(getColumnIndex(Database.activity_data.participants_max))));
//              "datetime_published" DATETIME,
//              "datetime_created" DATETIME NOT NULL,
        revision.setMaterial(getString(getColumnIndex(Database.activity_data.descr_material)));
        revision.setIntroduction(getString(getColumnIndex(Database.activity_data.descr_introduction)));
        revision.setPreparation(getString(getColumnIndex(Database.activity_data.descr_prepare)));
        revision.setDescription(getString(getColumnIndex(Database.activity_data.descr_activity)));
        revision.setSafety(getString(getColumnIndex(Database.activity_data.descr_safety)));
        revision.addDescriptionNote(getString(getColumnIndex(Database.activity_data.descr_notes)));
        revision.setAuthor(null);
        String sourceUri = getString(getColumnIndex(Database.activity_data.source_uri));
        if (sourceUri != null) {
            try {
                revision.setSourceURI(new URI(sourceUri));
            } catch (URISyntaxException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return revision;
    }

    public long getAuthorId() {
        return getLong(getColumnIndex(Database.activity_data.author_id));
    }

    public long getActivityId() {
        return getLong(getColumnIndex(Database.activity_data.activity_id));
    }
}
