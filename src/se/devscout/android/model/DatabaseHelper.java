package se.devscout.android.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import se.devscout.android.R;
import se.devscout.server.api.model.*;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.*;

public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;

    public SQLiteDatabase getDb() {
        if (db == null) {
            db = getWritableDatabase();
        }
        return db;
    }

    public Activity readActivity(ActivityKey key) {
        if (!mCacheActivity.containsKey(key.getId())) {
            readActivities();
        }
        return mCacheActivity.get(key.getId());
    }

    private static class User {

        final static String Id = "id";
        final static String EMail = "email";
        final static String EMailVerified = "email_verified";
        final static String PasswordHash = "password_hash";
        final static String PasswordAlgorithm = "password_algorithm";
        final static String DisplayName = "display_name";
    }

    private static final int VERSION = 1;

    private static final String NAME = "devscout.sqlite";
    private final Context mContext;
    private Toast mCurrentToast;
    private static final String STATUS_NEW = Status.NEW.name().substring(0, 1);
    private Map<Long, LocalActivity> mCacheActivity;
    private Map<Long, LocalUser> mCacheUser;
    private Map<Long, LocalCategory> mCacheCategory;
    private Map<Long, LocalMedia> mCacheMedia;
    private Map<Long, LocalReference> mCacheReference;

    public DatabaseHelper(Context context) {
        super(context, NAME, null, VERSION);
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

        values.put("uuid", bb.array());
        values.put("group_name", properties.getGroup());
        values.put("name", properties.getName());
        values.put("status", " ");
        values.put("owner_id", -1);
        return getDb().insertOrThrow("category", null, values);
    }

    public long createReference(ReferenceProperties properties) {
        try {
            ContentValues values = new ContentValues();
            values.put("type", properties.getType().name().substring(0, 1));
            values.put("uri", properties.getURI().toString());
            long id = getDb().insertOrThrow("reference", null, values);
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
        userValues.put("email", "testdata@devscout.se");
        userValues.put("display_name", "Testdata");
        return getDb().insertOrThrow("user", null, userValues);
    }

    private long createActivity(ActivityProperties properties, long owner) {
        ContentValues values = new ContentValues();
        values.put("owner_id", owner);
        values.put("status", STATUS_NEW);
        long activityId = getDb().insertOrThrow("activity", null, values);
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
        actRevValues.put("activity_id", activityId);
        actRevValues.put("status", STATUS_NEW);
        actRevValues.put("name", revision.getName());
        final SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        actRevValues.put("datetime_published", parser.format(new Date()));
        actRevValues.put("datetime_created", parser.format(new Date()));
        actRevValues.put("descr_material", revision.getDescriptionMaterial());
        actRevValues.put("descr_introduction", revision.getDescriptionIntroduction());
        actRevValues.put("descr_prepare", revision.getDescriptionPreparation());
        actRevValues.put("descr_activity", description);
        actRevValues.put("descr_safety", revision.getDescriptionSafety());
        actRevValues.put("descr_notes", descriptionNotes);
        Range<Integer> ages = revision.getAges();
        if (ages != null) {
            actRevValues.put("age_min", ages.getMin());
            actRevValues.put("age_max", ages.getMax());
        }
        Range<Integer> participants = revision.getParticipants();
        if (participants != null) {
            actRevValues.put("participants_min", participants.getMin());
            actRevValues.put("participants_max", participants.getMax());
        }
        Range<Integer> time = revision.getTimeActivity();
        if (time != null) {
            actRevValues.put("time_min", time.getMin());
            actRevValues.put("time_max", time.getMax());
        }
        actRevValues.put("featured", revision.isFeatured() ? 1 : 0);
        actRevValues.put("author_id", owner);
        actRevValues.put("source_uri", revision.getSourceURI() != null ? revision.getSourceURI().toString() : null);

        try {
            return getDb().insertOrThrow("activity_data", null, actRevValues);
        } catch (SQLException e) {
            logError(e, "Could not add this activity data: " + actRevValues);
            throw e;
        }
    }

    private void addActivityDataMedia(long activityDataId, boolean isFeatured, Media media) {
        Cursor cursor = getDb().query("media", new String[]{"id"}, "uri = ?", new String[]{media.getURI().toString()}, null, null, null);
        long mediaId;
        if (cursor.moveToFirst()) {
            mediaId = cursor.getLong(0);
        } else {
            ContentValues refValues = new ContentValues();
            refValues.put("status", STATUS_NEW);
            refValues.put("uri", media.getURI().toString());
            refValues.put("mime_type", "image/jpeg");
            mediaId = getDb().insertOrThrow("media", null, refValues);
        }
        if (mediaId >= 0) {
            ContentValues actDataRefValues = new ContentValues();
            actDataRefValues.put("activity_data_id", activityDataId);
            actDataRefValues.put("media_id", mediaId);
            actDataRefValues.put("featured", isFeatured);
            getDb().insertOrThrow("activity_data_media", null, actDataRefValues);
        }
        cursor.close();
    }

    private void addActivityDataReference(long activityDataId, ReferenceProperties reference) {
        Cursor cursor = getDb().query("reference", new String[]{"id"}, "uri = ? and type = ?", new String[]{reference.getURI().toString(), reference.getType().name().substring(0, 1)}, null, null, null);
        long refId;
        if (cursor.moveToFirst()) {
            refId = cursor.getLong(0);
        } else {
            ContentValues refValues = new ContentValues();
            refValues.put("uri", reference.getURI().toString());
            refValues.put("type", reference.getType().name().substring(0, 1));
            refId = getDb().insertOrThrow("reference", null, refValues);
        }
        if (refId >= 0) {
            ContentValues actDataRefValues = new ContentValues();
            actDataRefValues.put("activity_data_id", activityDataId);
            actDataRefValues.put("reference_id", refId);
            getDb().insertOrThrow("activity_data_reference", null, actDataRefValues);
        }
        cursor.close();
    }

    private void addActivityDataCategory(long activityDataId, String group, String name, long ownerId) {
        Cursor cursor = getDb().query("category", new String[]{"id"}, "group_name = ? and name = ?", new String[]{group, name}, null, null, null);
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

            refValues.put("status", STATUS_NEW);
            refValues.put("uuid", bb.array());
            refValues.put("group_name", group);
            refValues.put("name", name);
            refValues.put("owner_id", ownerId);
            catId = getDb().insertOrThrow("category", null, refValues);
        }
        if (catId >= 0) {
            ContentValues actDataCatValues = new ContentValues();
            actDataCatValues.put("activity_data_id", activityDataId);
            actDataCatValues.put("category_id", catId);
            getDb().insertOrThrow("activity_data_category", null, actDataCatValues);
        }
        cursor.close();
    }

    public Iterable<? extends LocalActivity> readActivities() {
        ArrayList<LocalActivity> activities = new ArrayList<LocalActivity>();
        Cursor cursor = getDb().rawQuery("select ad.id activity_data_id, a.*, ad.* from activity a inner join activity_data ad on a.id = ad.activity_id order by a.id, ad.id", null);
        long lastActivityId = 0;
        while (cursor.moveToNext()) {
            long activityId = cursor.getLong(cursor.getColumnIndex("id"));
            LocalActivity currentActivityToAdd = null;
            if (lastActivityId != activityId) {
                // Not same activity as last processed row
                if (!mCacheActivity.containsKey(activityId)) {
                    currentActivityToAdd = new LocalActivity(readUser(cursor.getLong(cursor.getColumnIndex("owner_id"))), activityId);
                    mCacheActivity.put(activityId, currentActivityToAdd);
                } else {
                    currentActivityToAdd = null;
                }
                activities.add(mCacheActivity.get(activityId));
            }
            if (currentActivityToAdd != null) {
                long activityDataId = cursor.getLong(cursor.getColumnIndex("activity_data_id"));
                LocalActivityRevision revision = new LocalActivityRevision(
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getInt(cursor.getColumnIndex("featured")) == 1,
                        currentActivityToAdd,
                        activityDataId
                );
                revision.setAges(new LocalRange(cursor.getInt(cursor.getColumnIndex("age_min")), cursor.getInt(cursor.getColumnIndex("age_max"))));
                revision.setTimeActivity(new LocalRange(cursor.getInt(cursor.getColumnIndex("time_min")), cursor.getInt(cursor.getColumnIndex("time_max"))));
                revision.setParticipants(new LocalRange(cursor.getInt(cursor.getColumnIndex("participants_min")), cursor.getInt(cursor.getColumnIndex("participants_max"))));
//              "datetime_published" DATETIME,
//              "datetime_created" DATETIME NOT NULL,
                revision.setMaterial(cursor.getString(cursor.getColumnIndex("descr_material")));
                revision.setIntroduction(cursor.getString(cursor.getColumnIndex("descr_introduction")));
                revision.setPreparation(cursor.getString(cursor.getColumnIndex("descr_prepare")));
                revision.setDescription(cursor.getString(cursor.getColumnIndex("descr_activity")));
                revision.setSafety(cursor.getString(cursor.getColumnIndex("descr_safety")));
                revision.addDescriptionNote(cursor.getString(cursor.getColumnIndex("descr_notes")));
                revision.setAuthor(readUser(cursor.getLong(cursor.getColumnIndex("author_id"))));
                String sourceUri = cursor.getString(cursor.getColumnIndex("source_uri"));
                if (sourceUri != null) {
                    try {
                        revision.setSourceURI(new URI(sourceUri));
                    } catch (URISyntaxException e) {
                        logError(e, "Could not parse URI stored in database");
                    }
                }

                Cursor catCursor = getDb().rawQuery("select c.* from category c inner join activity_data_category adc on c.id = adc.category_id where adc.activity_data_id = " + activityDataId, null);
                while (catCursor.moveToNext()) {
                    long categoryId = catCursor.getLong(catCursor.getColumnIndex("id"));
                    if (!mCacheCategory.containsKey(categoryId)) {
                        LocalCategory category = new LocalCategory(
                                catCursor.getString(catCursor.getColumnIndex("group_name")),
                                catCursor.getString(catCursor.getColumnIndex("name")),
                                categoryId);
                        mCacheCategory.put(categoryId, category);
                    }
                    revision.getCategories().add(mCacheCategory.get(categoryId));
                }
                catCursor.close();

                Cursor mediaCursor = getDb().rawQuery("select m.* from media m inner join activity_data_media adm on m.id = adm.media_id where adm.activity_data_id = " + activityDataId, null);
                while (mediaCursor.moveToNext()) {
                    long mediaId = mediaCursor.getLong(mediaCursor.getColumnIndex("id"));
                    if (!mCacheMedia.containsKey(mediaId)) {
                        try {
                            LocalMedia media = new LocalMedia(
                                    new URI(mediaCursor.getString(mediaCursor.getColumnIndex("uri"))),
                                    mediaCursor.getString(mediaCursor.getColumnIndex("mime_type")),
                                    mediaId);
                            mCacheMedia.put(mediaId, media);
                        } catch (URISyntaxException e) {
                            logError(e, "URI error");
                        }
                    }
                    revision.getMediaItems().add(mCacheMedia.get(mediaId));
                }
                mediaCursor.close();

                Cursor refCursor = getDb().rawQuery("select r.* from reference r inner join activity_data_reference adr on r.id = adr.reference_id where adr.activity_data_id = " + activityDataId, null);
                while (refCursor.moveToNext()) {
                    long refId = refCursor.getLong(refCursor.getColumnIndex("id"));
                    if (!mCacheReference.containsKey(refId)) {
                        try {
                            LocalReference ref = new LocalReference(
                                    refId,
                                    ReferenceType.READ_MORE/*refCursor.getString(refCursor.getColumnIndex("mime_type"))*/,
                                    new URI(refCursor.getString(refCursor.getColumnIndex("uri"))));
                            mCacheReference.put(refId, ref);
                        } catch (URISyntaxException e) {
                            logError(e, "URI error");
                        }
                    }
                    revision.getReferences().add(mCacheReference.get(refId));
                }
                refCursor.close();

                currentActivityToAdd.addRevisions(revision);

            }
            lastActivityId = activityId;
        }
        cursor.close();
        return activities;
    }

    private LocalUser readUser(long id) {
        if (!mCacheUser.containsKey(id)) {
            Cursor cursor = getReadableDatabase().rawQuery("select u.* from user u where u.id = " + id, null);
            if (cursor.moveToNext()) {
                LocalUser user = new LocalUser();
                user.setDisplayName(cursor.getString(cursor.getColumnIndex(User.DisplayName)));
                mCacheUser.put(id, user);
            }
            cursor.close();
        }
        return mCacheUser.get(id);
    }
}
