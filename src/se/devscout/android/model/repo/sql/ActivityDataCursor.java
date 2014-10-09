package se.devscout.android.model.repo.sql;

import android.database.Cursor;
import se.devscout.android.model.ActivityBean;
import se.devscout.android.model.IntegerRange;
import se.devscout.android.model.ObjectIdentifierBean;

import java.util.Date;

public class ActivityDataCursor extends BaseCursorWrapper {
    public ActivityDataCursor(Cursor cursor) {
        super(cursor);
    }

    public ActivityBean getActivityData() {
        ActivityBean revision = new ActivityBean(
                isNull(getColumnIndex(Database.activity.owner_id)) ? null : new ObjectIdentifierBean(getLong(getColumnIndex(Database.activity.owner_id))),
                getId(),
                getInt(getColumnIndex(Database.activity.server_id)),
                getInt(getColumnIndex(Database.activity.server_revision_id)),
                getInt(getColumnIndex(Database.activity.is_publishable)) != 0
        );
        revision.setFeatured(getInt(getColumnIndex(Database.activity.featured)) == 1);
        revision.setName(getString(getColumnIndex(Database.activity.name)));
        revision.setAges(new IntegerRange(getInt(getColumnIndex(Database.activity.age_min)), getInt(getColumnIndex(Database.activity.age_max))));
        revision.setTimeActivity(new IntegerRange(getInt(getColumnIndex(Database.activity.time_min)), getInt(getColumnIndex(Database.activity.time_max))));
        revision.setParticipants(new IntegerRange(getInt(getColumnIndex(Database.activity.participants_min)), getInt(getColumnIndex(Database.activity.participants_max))));
        revision.setDateCreated(new Date(getLong(getColumnIndex(Database.activity.datetime_created))));
//        revision.setDatePublished(new Date(getLong(getColumnIndex(Database.activity.datetime_published))));
//              "datetime_published" DATETIME,
//              "datetime_created" DATETIME NOT NULL,

//        revision.setServerId(getInt(getColumnIndex(Database.activity.server_id)));
//        revision.setServerRevisionId(getInt(getColumnIndex(Database.activity.server_revision_id)));
//        revision.setPublishable(getInt(getColumnIndex(Database.activity.is_publishable)) != 0);

        revision.setMaterial(getString(getColumnIndex(Database.activity.descr_material)));
        revision.setIntroduction(getString(getColumnIndex(Database.activity.descr_introduction)));
        revision.setPreparation(getString(getColumnIndex(Database.activity.descr_prepare)));
        revision.setDescription(getString(getColumnIndex(Database.activity.descr_activity)));
        revision.setSafety(getString(getColumnIndex(Database.activity.descr_safety)));
        revision.addDescriptionNote(getString(getColumnIndex(Database.activity.descr_notes)));
        revision.setFavouritesCount(isNull(getColumnIndex(Database.activity.favourite_count)) ? null : getInt(getColumnIndex(Database.activity.favourite_count)));
/*
        revision.setAuthor(null);
        String sourceUri = getString(getColumnIndex(Database.activity_data.source_uri));
        if (sourceUri != null) {
            try {
                revision.setSourceURI(new URI(sourceUri));
            } catch (URISyntaxException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
*/
        return revision;
    }

/*
    public long getAuthorId() {
        return getLong(getColumnIndex(Database.activity_data.author_id));
    }
*/
}
