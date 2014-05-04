package se.devscout.android.model.repo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import se.devscout.android.model.IntegerRangePojo;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.UserKey;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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

    public static class QueryBuilder {
        private List<String> mWhere = new ArrayList<String>();
        private List<String> mSelect = new ArrayList<String>();
        private StringBuilder mFrom = new StringBuilder();

        public QueryBuilder() {
            mSelect.add("a." + Database.activity.owner_id);
            mSelect.add("ad." + Database.activity_data.id);
            mSelect.add("ad." + Database.activity_data.activity_id);
            mSelect.add("ad." + Database.activity_data.status);
            mSelect.add("ad." + Database.activity_data.name);
            mSelect.add("ad." + Database.activity_data.datetime_published);
            mSelect.add("ad." + Database.activity_data.datetime_created);
            mSelect.add("ad." + Database.activity_data.descr_material);
            mSelect.add("ad." + Database.activity_data.descr_introduction);
            mSelect.add("ad." + Database.activity_data.descr_prepare);
            mSelect.add("ad." + Database.activity_data.descr_activity);
            mSelect.add("ad." + Database.activity_data.descr_safety);
            mSelect.add("ad." + Database.activity_data.descr_notes);
            mSelect.add("ad." + Database.activity_data.age_min);
            mSelect.add("ad." + Database.activity_data.age_max);
            mSelect.add("ad." + Database.activity_data.participants_min);
            mSelect.add("ad." + Database.activity_data.participants_max);
            mSelect.add("ad." + Database.activity_data.time_min);
            mSelect.add("ad." + Database.activity_data.time_max);
            mSelect.add("ad." + Database.activity_data.featured);
            mSelect.add("ad." + Database.activity_data.author_id);
            mSelect.add("ad." + Database.activity_data.source_uri);
            mFrom.append("   " + Database.activity.T + " a " +
                    "   inner join " + Database.activity_data.T + " admax on a." + Database.activity.id + " = admax." + Database.activity_data.activity_id + " " +
                    "   inner join " + Database.activity_data.T + " ad on a." + Database.activity.id + " = ad." + Database.activity_data.activity_id + " ");
        }

        public ActivityDataCursor query(SQLiteDatabase db) {
            StringBuilder sb = new StringBuilder();
            sb.append("" +
                    " select " +
                    "   " + TextUtils.join(", ", mSelect) +
                    " from " +
                    "   " + mFrom.toString());
            if (!mWhere.isEmpty()) {
                sb.append("" +
                        " where " + TextUtils.join(" and ", mWhere));
            }
            sb.append("" +
                    " group by " +
                    "   a." + Database.activity.id + ", " +
                    "   ad." + Database.activity_data.id + " " +
                    " having " +
                    "   ad." + Database.activity_data.id + " = max(admax." + Database.activity_data.id + ") " +
                    " order by " +
                    "   a." + Database.activity.id + ", " +
                    "   ad." + Database.activity_data.id);
            return new ActivityDataCursor(db.rawQuery(sb.toString(), null));
        }

        public QueryBuilder addWhereFavourite(UserKey userKey) {
            mFrom.append("" +
                    "   inner join " + Database.favourite_activity.T + " fa on fa." + Database.favourite_activity.activity_id + " = a." + Database.activity.id + " and fa." + Database.favourite_activity.user_id + " = " + userKey.getId());
            return this;
        }

        public QueryBuilder addWhereIsFeatured(boolean featured) {
            return addWhere("ad." + Database.activity_data.featured + " = 1");
        }

        private QueryBuilder addWhere(String expr) {
            mWhere.add(expr);
            return this;
        }
    }
}
