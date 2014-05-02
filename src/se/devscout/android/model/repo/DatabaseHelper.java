package se.devscout.android.model.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.*;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import se.devscout.android.R;
import se.devscout.android.util.IsFeaturedFilter;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.*;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.*;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final SQLiteDatabase.CursorFactory LOGGING_CURSOR_FACTORY = new SQLiteDatabase.CursorFactory() {
        @Override
        public Cursor newCursor(SQLiteDatabase sqLiteDatabase, SQLiteCursorDriver sqLiteCursorDriver, String s, SQLiteQuery sqLiteQuery) {
            Log.d(DatabaseHelper.class.getName(), sqLiteQuery.toString());
            return new SQLiteCursor(sqLiteCursorDriver, s, sqLiteQuery);
        }
    };
    private SQLiteDatabase db;

    public SQLiteDatabase getDb() {
        if (db == null) {
            db = getWritableDatabase();
        }
        return db;
    }

    public Activity readActivity(ActivityKey key) {
        if (!mCacheActivity.containsKey(key.getId())) {
            readActivities(null);
        }
        return mCacheActivity.get(key.getId());
    }

    private static final int VERSION = 1;

    private static final String NAME = "devscout.sqlite";
    private final Context mContext;
    private Toast mCurrentToast;
    private static final String STATUS_NEW = Status.NEW.name().substring(0, 1);

    //TODO: Everything is added to the cache (it is never cleared/purged). This is obviously not good.
    private Map<Long, LocalActivity> mCacheActivity;
    private Map<Long, LocalUser> mCacheUser;
    private Map<Long, LocalCategory> mCacheCategory;
    private Map<Long, LocalMedia> mCacheMedia;
    private Map<Long, LocalReference> mCacheReference;

    public DatabaseHelper(Context context) {
        super(context, NAME, LOGGING_CURSOR_FACTORY, VERSION);
        mContext = context;
        clearCaches();
    }

    private void clearCaches() {
        mCacheActivity = new HashMap<Long, LocalActivity>();
        mCacheUser = new HashMap<Long, LocalUser>();
        mCacheCategory = new HashMap<Long, LocalCategory>();
        mCacheMedia = new HashMap<Long, LocalMedia>();
        mCacheReference = new HashMap<Long, LocalReference>();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        logInfo("Starting initialising database.");
        db.beginTransaction();
        try {
            InputStream inputStream = mContext.getResources().openRawResource(R.raw.database);
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter(";");
            while (scanner.hasNext()) {
                String cmd = scanner.next();
                cmd = cmd.replaceAll("\\s*--.*", "").trim();
                if (cmd.toUpperCase().startsWith("BEGIN") || cmd.toUpperCase().startsWith("COMMIT")) {
                    logDebug("Ignoring SQL: " + cmd);
                    continue;
                } else {
                    logDebug("Executing SQL: " + cmd);
                    db.execSQL(cmd);
                }
            }
            inputStream.close();
            db.setTransactionSuccessful();
        } catch (Throwable e) {
            logError(e, "Error!");
        } finally {
            db.endTransaction();
        }
        logInfo("Done initialising database.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void logInfo(String msg) {
        Log.i(DatabaseHelper.class.getName(), msg);
        showToast(msg);
    }

    private void logDebug(String msg) {
        Log.d(DatabaseHelper.class.getName(), msg);
    }

    private void logError(Throwable e, String msg) {
        Log.e(DatabaseHelper.class.getName(), msg, e);
        showToast(msg);
    }

    private void showToast(String msg) {
        if (mCurrentToast != null) {
            mCurrentToast.cancel();
        }
        mCurrentToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        mCurrentToast.show();
    }

    public long createCategory(CategoryProperties properties) {
        ContentValues values = new ContentValues();

        byte[] bytes = new byte[16];
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        bb.order(ByteOrder.LITTLE_ENDIAN);//) or ByteOrder.BIG_ENDIAN);
        UUID uuid = UUID.randomUUID();
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        // to reverse
//        bb.flip();
//        UUID uuid = new UUID(bb.getLong(), bb.getLong());

        values.put(Database.category.uuid, bb.array());
        values.put(Database.category.group_name, properties.getGroup());
        values.put(Database.category.name, properties.getName());
        values.put(Database.category.status, " ");
        values.put(Database.category.owner_id, -1);
        return getDb().insertOrThrow(Database.category.T, null, values);
    }

    public long createReference(ReferenceProperties properties) {
        try {
            ContentValues values = new ContentValues();
            values.put(Database.reference.type, properties.getType().name().substring(0, 1));
            values.put(Database.reference.uri, properties.getURI().toString());
            long id = getDb().insertOrThrow(Database.reference.T, null, values);
            logInfo("Created reference #" + id);
            return id;
        } catch (SQLiteException e) {
            logError(e, "Could not create reference");
            throw e;
        }
    }

    public void dropDatabase(boolean addTestData) {
        getDb().close();
        mContext.deleteDatabase(NAME);
        db = null;
//        db = mContext.openOrCreateDatabase(NAME, VERSION, null);

        clearCaches();

        getDb().beginTransaction();
        try {
            long userId = createUser();

            List<LocalActivity> testActivities = TestDataUtil.readXMLTestData(mContext);
            for (Activity testActivity : testActivities) {
                long activityId = createActivity(testActivity, userId);
            }
            getDb().setTransactionSuccessful();
        } catch (SQLException e) {
            logError(e, "Error when recreating database");
        } finally {
            getDb().endTransaction();
        }
    }

    private long createUser() {
        ContentValues userValues = new ContentValues();
        userValues.put(Database.user.email, "testdata@devscout.se");
        userValues.put(Database.user.display_name, "Testdata");
        return getDb().insertOrThrow(Database.user.T, null, userValues);
    }

    private long createActivity(ActivityProperties properties, long owner) {
        ContentValues values = new ContentValues();
        values.put(Database.activity.owner_id, owner);
        values.put(Database.activity.status, STATUS_NEW);
        long activityId = getDb().insertOrThrow(Database.activity.T, null, values);
        for (ActivityRevision revision : properties.getRevisions()) {
            long activityDataId = addActivityData(owner, activityId, revision);

            for (Category category : revision.getCategories()) {
                addActivityDataCategory(activityDataId, category.getGroup(), category.getName(), owner);
            }
            boolean first = true;
            for (Media media : revision.getMediaItems()) {
                addActivityDataMedia(activityDataId, first, media);
                first = false;
            }
            for (ReferenceProperties reference : revision.getReferences()) {
                addActivityDataReference(activityDataId, reference);
            }
        }
        return activityId;
    }

    private long addActivityData(long owner, long activityId, ActivityRevision revision) {

//        logDebug("addActivityData " + activityId + " " + revision.getName());

        String description = revision.getDescription();
        String descriptionNotes = revision.getDescriptionNotes();

        if (description == null || description.length() == 0) {
            if (descriptionNotes != null && descriptionNotes.length() > 0) {
                logDebug("Using notes as description");
                description = descriptionNotes;
                descriptionNotes = null;
            } else {
                logInfo("Activity lacks description and notes.");
                description = "";
            }
        }

        ContentValues actRevValues = new ContentValues();
        actRevValues.put(Database.activity_data.activity_id, activityId);
        actRevValues.put(Database.activity_data.status, STATUS_NEW);
        actRevValues.put(Database.activity_data.name, revision.getName());
        final SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        actRevValues.put(Database.activity_data.datetime_published, parser.format(new Date()));
        actRevValues.put(Database.activity_data.datetime_created, parser.format(new Date()));
        actRevValues.put(Database.activity_data.descr_material, revision.getDescriptionMaterial());
        actRevValues.put(Database.activity_data.descr_introduction, revision.getDescriptionIntroduction());
        actRevValues.put(Database.activity_data.descr_prepare, revision.getDescriptionPreparation());
        actRevValues.put(Database.activity_data.descr_activity, description);
        actRevValues.put(Database.activity_data.descr_safety, revision.getDescriptionSafety());
        actRevValues.put(Database.activity_data.descr_notes, descriptionNotes);
        Range<Integer> ages = revision.getAges();
        if (ages != null) {
            actRevValues.put(Database.activity_data.age_min, ages.getMin());
            actRevValues.put(Database.activity_data.age_max, ages.getMax());
        }
        Range<Integer> participants = revision.getParticipants();
        if (participants != null) {
            actRevValues.put(Database.activity_data.participants_min, participants.getMin());
            actRevValues.put(Database.activity_data.participants_max, participants.getMax());
        }
        Range<Integer> time = revision.getTimeActivity();
        if (time != null) {
            actRevValues.put(Database.activity_data.time_min, time.getMin());
            actRevValues.put(Database.activity_data.time_max, time.getMax());
        }
        actRevValues.put(Database.activity_data.featured, revision.isFeatured() ? 1 : 0);
        actRevValues.put(Database.activity_data.author_id, owner);
        actRevValues.put(Database.activity_data.source_uri, revision.getSourceURI() != null ? revision.getSourceURI().toString() : null);

        try {
            return getDb().insertOrThrow(Database.activity_data.T, null, actRevValues);
        } catch (SQLException e) {
            logError(e, "Could not add this activity data: " + actRevValues);
            throw e;
        }
    }

    private void addActivityDataMedia(long activityDataId, boolean isFeatured, Media media) {
        Cursor cursor = getDb().query(Database.media.T, new String[]{Database.media.id}, Database.media.uri + " = ?", new String[]{media.getURI().toString()}, null, null, null);
        long mediaId;
        if (cursor.moveToFirst()) {
            mediaId = cursor.getLong(0);
        } else {
            ContentValues refValues = new ContentValues();
            refValues.put(Database.media.status, STATUS_NEW);
            refValues.put(Database.media.uri, media.getURI().toString());
            refValues.put(Database.media.mime_type, "image/jpeg");
            mediaId = getDb().insertOrThrow(Database.media.T, null, refValues);
        }
        if (mediaId >= 0) {
            ContentValues actDataRefValues = new ContentValues();
            actDataRefValues.put(Database.activity_data_media.activity_data_id, activityDataId);
            actDataRefValues.put(Database.activity_data_media.media_id, mediaId);
            actDataRefValues.put(Database.activity_data_media.featured, isFeatured);
            getDb().insertOrThrow(Database.activity_data_media.T, null, actDataRefValues);
        }
        cursor.close();
    }

    private void addActivityDataReference(long activityDataId, ReferenceProperties reference) {
        Cursor cursor = getDb().query(Database.reference.T, new String[]{Database.reference.id}, Database.reference.uri + " = ? and " + Database.reference.type + " = ?", new String[]{reference.getURI().toString(), reference.getType().name().substring(0, 1)}, null, null, null);
        long refId;
        if (cursor.moveToFirst()) {
            refId = cursor.getLong(0);
        } else {
            ContentValues refValues = new ContentValues();
            refValues.put(Database.reference.uri, reference.getURI().toString());
            refValues.put(Database.reference.type, reference.getType().name().substring(0, 1));
            refId = getDb().insertOrThrow(Database.reference.T, null, refValues);
        }
        if (refId >= 0) {
            ContentValues actDataRefValues = new ContentValues();
            actDataRefValues.put(Database.activity_data_reference.activity_data_id, activityDataId);
            actDataRefValues.put(Database.activity_data_reference.reference_id, refId);
            getDb().insertOrThrow(Database.activity_data_reference.T, null, actDataRefValues);
        }
        cursor.close();
    }

    private void addActivityDataCategory(long activityDataId, String group, String name, long ownerId) {
        Cursor cursor = getDb().query(Database.category.T, new String[]{Database.category.id}, Database.category.group_name + " = ? and " + Database.category.name + " = ?", new String[]{group, name}, null, null, null);
        long catId;
        if (cursor.moveToFirst()) {
            catId = cursor.getLong(0);
        } else {
            ContentValues refValues = new ContentValues();
            byte[] bytes = new byte[16];
            ByteBuffer bb = ByteBuffer.wrap(bytes);
            bb.order(ByteOrder.LITTLE_ENDIAN);//) or ByteOrder.BIG_ENDIAN);
            UUID uuid = UUID.randomUUID();
            bb.putLong(uuid.getMostSignificantBits());
            bb.putLong(uuid.getLeastSignificantBits());

            // to reverse
//        bb.flip();
//        UUID uuid = new UUID(bb.getLong(), bb.getLong());

            refValues.put(Database.category.status, STATUS_NEW);
            refValues.put(Database.category.uuid, bb.array());
            refValues.put(Database.category.group_name, group);
            refValues.put(Database.category.name, name);
            refValues.put(Database.category.owner_id, ownerId);
            catId = getDb().insertOrThrow(Database.category.T, null, refValues);
        }
        if (catId >= 0) {
            ContentValues actDataCatValues = new ContentValues();
            actDataCatValues.put(Database.activity_data_category.activity_data_id, activityDataId);
            actDataCatValues.put(Database.activity_data_category.category_id, catId);
            getDb().insertOrThrow(Database.activity_data_category.T, null, actDataCatValues);
        }
        cursor.close();
    }

    public Iterable<? extends LocalActivity> readActivities(ActivityFilter filter) {
        List<String> sqlWhere = new ArrayList<String>();
        if (filter instanceof IsFeaturedFilter) {
            sqlWhere.add("ad." + Database.activity_data.featured + " = 1");
        }
        ArrayList<LocalActivity> activities = new ArrayList<LocalActivity>();
        ActivityDataCursor cursor = new ActivityDataCursor(getDb().rawQuery("" +
                "select " +
                "   a." + Database.activity.owner_id + ", " +
                "   ad." + Database.activity_data.id + ", " +
                "   ad." + Database.activity_data.activity_id + ", " +
                "   ad." + Database.activity_data.status + ", " +
                "   ad." + Database.activity_data.name + ", " +
                "   ad." + Database.activity_data.datetime_published + ", " +
                "   ad." + Database.activity_data.datetime_created + ", " +
                "   ad." + Database.activity_data.descr_material + ", " +
                "   ad." + Database.activity_data.descr_introduction + ", " +
                "   ad." + Database.activity_data.descr_prepare + ", " +
                "   ad." + Database.activity_data.descr_activity + ", " +
                "   ad." + Database.activity_data.descr_safety + ", " +
                "   ad." + Database.activity_data.descr_notes + ", " +
                "   ad." + Database.activity_data.age_min + ", " +
                "   ad." + Database.activity_data.age_max + ", " +
                "   ad." + Database.activity_data.participants_min + ", " +
                "   ad." + Database.activity_data.participants_max + ", " +
                "   ad." + Database.activity_data.time_min + ", " +
                "   ad." + Database.activity_data.time_max + ", " +
                "   ad." + Database.activity_data.featured + ", " +
                "   ad." + Database.activity_data.author_id + ", " +
                "   ad." + Database.activity_data.source_uri + " " +
                "from " +
                "   " + Database.activity.T + " a " +
                "   inner join " + Database.activity_data.T + " admax on a." + Database.activity.id + " = admax." + Database.activity_data.activity_id + " " +
                "   inner join " + Database.activity_data.T + " ad on a." + Database.activity.id + " = ad." + Database.activity_data.activity_id + " " +
                (!sqlWhere.isEmpty() ? "where " + TextUtils.join(" and ", sqlWhere) : "") + " " +
                "group by " +
                "   a." + Database.activity.id + ", " +
                "   ad." + Database.activity_data.id + " " +
                "having " +
                "   ad." + Database.activity_data.id + " = max(admax." + Database.activity_data.id + ") " +
                "order by " +
                "   a." + Database.activity.id + ", " +
                "   ad." + Database.activity_data.id, null));
        long lastActivityId = 0;
        while (cursor.moveToNext()) {
            long activityId = cursor.getActivityId();
            LocalActivity currentActivityToAdd = null;
            if (lastActivityId != activityId) {
                // Not same activity as last processed row
                if (!mCacheActivity.containsKey(activityId)) {
                    currentActivityToAdd = new LocalActivity(readUser(cursor.getLong(cursor.getColumnIndex("owner_id"))), activityId);
                    mCacheActivity.put(activityId, currentActivityToAdd);
                }
                activities.add(mCacheActivity.get(activityId));
            }
            //TODO Should the cache be cleared? Are the revisions lists getting longer and longer for each search?
            currentActivityToAdd = mCacheActivity.get(activityId);

            boolean revisionExistsInActivity = false;
            for (LocalActivityRevision revision : currentActivityToAdd.getRevisions()) {
                if (revision.getId() == cursor.getId()) {
                    revisionExistsInActivity = true;
                    break;
                }
            }
            if (!revisionExistsInActivity) {
                LocalActivityRevision revision = cursor.getActivityData(currentActivityToAdd);

                revision.setAuthor(readUser(cursor.getAuthorId()));

                initActivityDataCategories(revision);

                initActivityDataMedia(revision);

                initActivityDataReferences(revision);

                currentActivityToAdd.addRevisions(revision);

            }
            lastActivityId = activityId;
        }
        cursor.close();
        return activities;
    }

    private void initActivityDataReferences(LocalActivityRevision revision) {
        ReferenceCursor refCursor = new ReferenceCursor(getDb().rawQuery("" +
                "select " +
                "   r.* " +
                "from " +
                "   " + Database.reference.T + " r inner join " + Database.activity_data_reference.T + " adr on r.id = adr.reference_id " +
                "where " +
                "   adr.activity_data_id = " + revision.getId(), null));
        while (refCursor.moveToNext()) {
            try {
                long refId = refCursor.getId();
                if (!mCacheReference.containsKey(refId)) {
                    mCacheReference.put(refId, refCursor.getReference());
                }
                revision.getReferences().add(mCacheReference.get(refId));
            } catch (URISyntaxException e) {
                logError(e, "Invalid URI");
            }
        }
        refCursor.close();
    }

    private void initActivityDataMedia(LocalActivityRevision revision) {
        MediaCursor mediaCursor = new MediaCursor(getDb().rawQuery("" +
                "select " +
                "   m.* " +
                "from " +
                "   " + Database.media.T + " m inner join " + Database.activity_data_media.T + " adm on m.id = adm.media_id " +
                "where " +
                "   adm.activity_data_id = " + revision.getId(), null));
        while (mediaCursor.moveToNext()) {
            try {
                long mediaId = mediaCursor.getId();
                if (!mCacheMedia.containsKey(mediaId)) {
                    mCacheMedia.put(mediaId, mediaCursor.getMedia());
                }
                revision.getMediaItems().add(mCacheMedia.get(mediaId));
            } catch (URISyntaxException e) {
                logError(e, "Invalid URI");
            }
        }
        mediaCursor.close();
    }

    private void initActivityDataCategories(LocalActivityRevision revision) {
        CategoryCursor catCursor = new CategoryCursor(getDb().rawQuery("" +
                "select " +
                "   c.* " +
                "from " +
                "   " + Database.category.T + " c inner join " + Database.activity_data_category.T + " adc on c.id = adc.category_id " +
                "where " +
                "   adc.activity_data_id = " + revision.getId(), null));
        while (catCursor.moveToNext()) {
            long categoryId = catCursor.getId();
            if (!mCacheCategory.containsKey(categoryId)) {
                mCacheCategory.put(categoryId, catCursor.getCategory());
            }
            revision.getCategories().add(mCacheCategory.get(categoryId));
        }
        catCursor.close();
    }

    private LocalUser readUser(long id) {
        if (!mCacheUser.containsKey(id)) {
            Cursor cursor = getReadableDatabase().rawQuery("" +
                    "select " +
                    "   u.* " +
                    "from " +
                    "   " + Database.user.T + " u " +
                    "where " +
                    "   u.id = " + id, null);
            if (cursor.moveToNext()) {
                LocalUser user = new LocalUser();
                user.setDisplayName(cursor.getString(cursor.getColumnIndex(Database.user.display_name)));
                mCacheUser.put(id, user);
            }
            cursor.close();
        }
        return mCacheUser.get(id);
    }
}
