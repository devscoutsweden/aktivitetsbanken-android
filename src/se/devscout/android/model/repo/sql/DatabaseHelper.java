package se.devscout.android.model.repo.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.*;
import android.preference.PreferenceManager;
import android.widget.Toast;
import se.devscout.android.R;
import se.devscout.android.model.*;
import se.devscout.android.model.repo.TestDataUtil;
import se.devscout.android.model.repo.sql.cache.ActivityIdCache;
import se.devscout.android.model.repo.sql.cache.CategoryIdCache;
import se.devscout.android.model.repo.sql.cache.MediaIdCache;
import se.devscout.android.model.repo.sql.cache.ReferenceIdCache;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.PreferencesUtil;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.activityfilter.AndFilter;
import se.devscout.server.api.activityfilter.OrFilter;
import se.devscout.server.api.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final SQLiteDatabase.CursorFactory LOGGING_CURSOR_FACTORY = new SQLiteDatabase.CursorFactory() {
        @Override
        public Cursor newCursor(SQLiteDatabase sqLiteDatabase, SQLiteCursorDriver sqLiteCursorDriver, String s, SQLiteQuery sqLiteQuery) {
            LogUtil.d(DatabaseHelper.class.getName(), sqLiteQuery.toString());
            return new SQLiteCursor(sqLiteCursorDriver, s, sqLiteQuery);
        }
    };
    private SQLiteDatabase db;
    private Set<Long> mBannedSearchHistoryIds = new HashSet<Long>();
    private ActivityIdCache mActivityIdCache = new ActivityIdCache(this);
    private CategoryIdCache mCategoryIdCache = new CategoryIdCache(this);
    private MediaIdCache mMediaIdCache = new MediaIdCache(this);
    private ReferenceIdCache mReferenceIdCache = new ReferenceIdCache(this);

    /**
     * This method is synchronized as to prevent multiple "search threads" from
     * trying to access, and consequently trying to create, the database at the
     * same time. One thread gets to create the database and the other simply
     * have to wait for it to finish.
     */
    public synchronized SQLiteDatabase getDb() {
        if (db == null) {
            db = getWritableDatabase();
        }
        return db;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    public List<ActivityBean> readActivities(ActivityKey... keys) {
        List<ActivityBean> res = new ArrayList<>();
        List<ActivityKey> missing = new ArrayList<>();
        for (ActivityKey key : keys) {
            if (!mCacheActivity.containsKey(key.getId())) {
                missing.add(key);
            }
        }
        if (!missing.isEmpty()) {
            readActivities(new SQLKeysFilter(missing.toArray(new ActivityKey[missing.size()])));
        }
        for (ActivityKey key : keys) {
            res.add(mCacheActivity.get(key.getId()));
        }
        return res;
    }

    private static final int VERSION = 1;

    private static final String NAME = "devscout.sqlite";
    private final Context mContext;
    private Toast mCurrentToast;
    private static final String STATUS_NEW = Status.NEW.name().substring(0, 1);

    //TODO: Everything is added to the cache (it is never cleared/purged). This is obviously not good.
    // todo: How much does DatabaseHelper.mCacheCategory and DatabaseHelper.mActivityIdCache overlap?
    private Map<Long, ActivityBean> mCacheActivity;
    private Map<Long, UserBean> mCacheUser;
    // todo: How much does DatabaseHelper.mCacheCategory and DatabaseHelper.mCategoryIdCache overlap?
    private Map<Long, CategoryBean> mCacheCategory;
    private Map<Long, MediaBean> mCacheMedia;
    private Map<Long, ReferenceBean> mCacheReference;

    private static int[] DATABASE_MIGRATION_SCRIPTS = {
            R.raw.db_migrate_0_create_server_database,
            R.raw.db_migrate_1_category_icon
    };

    public DatabaseHelper(Context context) {
        super(context, NAME, LOGGING_CURSOR_FACTORY, DATABASE_MIGRATION_SCRIPTS.length);
        mContext = context;
        clearCaches();
    }

    private void clearCaches() {
        mCacheActivity = new HashMap<Long, ActivityBean>();
        mCacheUser = new HashMap<Long, UserBean>();
        mCacheCategory = new HashMap<Long, CategoryBean>();
        mCacheMedia = new HashMap<Long, MediaBean>();
        mCacheReference = new HashMap<Long, ReferenceBean>();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        logInfo("Starting initialising database.");
        db.beginTransaction();
        try {
            executeSQLScripts(db, 0, DATABASE_MIGRATION_SCRIPTS.length);

            String apiKey = PreferenceManager.getDefaultSharedPreferences(mContext).getString("api_key", null);
            long anonymousUserId = createUser(new UserPropertiesBean("Anonymous", apiKey, 0L, 0L, false), db);
            PreferencesUtil.getInstance(mContext).setCurrentUser(new ObjectIdentifierBean(anonymousUserId));

            db.setTransactionSuccessful();
            logInfo("Done initialising database.");
        } catch (RuntimeException e) {
            logError(e, "Failed initialising database.");
            throw e;
        } finally {
            db.endTransaction();
        }
    }

    private void executeSQLScript(SQLiteDatabase db, int sqlScriptResId) {
        InputStream inputStream = null;
        try {
            inputStream = mContext.getResources().openRawResource(sqlScriptResId);
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter(";");
            while (scanner.hasNext()) {
                String cmd = scanner.next();
                cmd = cmd.replaceAll("\\s*--.*", "").trim();
                if (cmd.length() > 0) {
                    logDebug("Executing SQL: " + cmd);
                    try {
                        db.execSQL(cmd);
                    } catch (SQLException e) {
                        if (e.getMessage() != null && e.getMessage().contains("not an error")) {
                            logDebug("Ignoring exception containing text 'not an error'.");
                        } else {
                            logError(e, "Error when executing SQL command");
                            throw e;
                        }
                    }
                } else {
                    logDebug("Ignoring empty SQL statement.");
                }
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logError(e, "Failed to close SQL script file");
                }
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        logInfo("Starting to upgrade database.");
        try {
            db.beginTransaction();
            executeSQLScripts(db, oldVersion, newVersion);
            db.setTransactionSuccessful();
            logInfo("Done upgrading database.");
        } catch (RuntimeException e) {
            logError(e, "Failed to upgrade database.");
            throw e;
        } finally {
            db.endTransaction();
        }
    }

    private void executeSQLScripts(SQLiteDatabase db, int fromInclusive, int toExclusive) {
        for (int i = fromInclusive; i < toExclusive; i++) {
            executeSQLScript(db, DATABASE_MIGRATION_SCRIPTS[i]);
        }
    }

    private void logInfo(String msg) {
        LogUtil.i(DatabaseHelper.class.getName(), msg);
    }

    private void logDebug(String msg) {
        LogUtil.d(DatabaseHelper.class.getName(), msg);
    }

    private void logError(Throwable e, String msg) {
        LogUtil.e(DatabaseHelper.class.getName(), msg, e);
    }

    public long createCategory(CategoryProperties properties) {
        try {
            ContentValues values = createContentValues(properties);
            long id = getDb().insertOrThrow(Database.category.T, null, values);
            mCategoryIdCache.onInsert(id, properties);
            logInfo("Created category #" + id);
            return id;
        } catch (SQLiteException e) {
            logError(e, "Could not create category");
            throw e;
        }
    }

    public long createReference(ReferenceProperties properties) {
        try {
            ContentValues values = createContentValues(properties);
            long id = getDb().insertOrThrow(Database.reference.T, null, values);
            logInfo("Created reference #" + id);
            mReferenceIdCache.onInsert(id, properties);
            return id;
        } catch (SQLiteException e) {
            logError(e, "Could not create reference");
            throw e;
        }
    }

    public long createMediaItem(MediaProperties properties) {
        try {
            ContentValues values = createContentValues(properties);
            long id = getDb().insertOrThrow(Database.media.T, null, values);
            logInfo("Created media item #" + id);
            mMediaIdCache.onInsert(id, properties);
            return id;
        } catch (SQLiteException e) {
            logError(e, "Could not create media item");
            throw e;
        }
    }

    private ContentValues createContentValues(ReferenceProperties properties) {
        ContentValues values = new ContentValues();
        values.put(Database.reference.server_id, properties.getServerId());
        values.put(Database.reference.server_revision_id, properties.getServerRevisionId());
        values.put(Database.reference.description, properties.getDescription());
        values.put(Database.reference.uri, properties.getURI().toString());
        return values;
    }

    public void dropDatabase(boolean addTestData) {
        getDb().close();
        mContext.deleteDatabase(NAME);
        db = null;
//        db = mContext.openOrCreateDatabase(NAME, VERSION, null);

        clearCaches();

        getDb().beginTransaction();
        try {
            if (addTestData) {
                List<ActivityBean> testActivities = TestDataUtil.readXMLTestData(mContext);
                for (Activity testActivity : testActivities) {
                    long activityId = createActivity(testActivity);
                }
            }
            getDb().setTransactionSuccessful();
        } catch (SQLException e) {
            logError(e, "Error when recreating database");
        } finally {
            getDb().endTransaction();
        }
    }

    private long createUser(UserProperties properties) {
        return createUser(properties, getDb());
    }

    private long createUser(UserProperties properties, SQLiteDatabase database) {
        ContentValues userValues = new ContentValues();
        userValues.put(Database.user.server_id, properties.getServerId());
        userValues.put(Database.user.server_revision_id, properties.getServerRevisionId());
        userValues.put(Database.user.display_name, properties.getDisplayName());
        userValues.put(Database.user.api_key, properties.getAPIKey());
//        userValues.put(Database.user.email_verified, properties.isEmailAddressVerified());
//        userValues.put(Database.user.password_algorithm, properties.getPasswordHashAlgorithm());
//        userValues.put(Database.user.password_hash, "Testdata");
        return database.insertOrThrow(Database.user.T, null, userValues);
    }

    public long createActivity(ActivityProperties properties) {
        ContentValues values = createContentValues(properties);

        long activityId = getDb().insertOrThrow(Database.activity.T, null, values);

        mActivityIdCache.onInsert(activityId, properties);

        addCategoriesToActivity(activityId, properties.getCategories());

        addReferencesToActivity(activityId, properties.getReferences());

        addMediaItemsToActivity(activityId, properties.getMediaItems());

        return activityId;
    }

    public boolean updateActivity(ActivityKey key, ActivityProperties properties) {
        ContentValues values = createContentValues(properties);

        if (getDb().update(Database.activity.T, values, Database.activity.id + "=" + key.getId(), null) != 1) {
            logInfo("Could not update activity " + key.getId());
            return false;
        }
        mActivityIdCache.onUpdate(properties);
        mCacheActivity.remove(key.getId());


        // Associated categories
        deleteCategoriesFromActivity(key);
        addCategoriesToActivity(key.getId(), properties.getCategories());

        // Associated media items
        deleteMediaItemsFromActivity(key);
        addMediaItemsToActivity(key.getId(), properties.getMediaItems());

        // Associated references
        deleteReferencesFromActivity(key);
        addReferencesToActivity(key.getId(), properties.getReferences());

        return true;
    }

    public boolean updateCategory(CategoryKey key, CategoryProperties properties) {
        ContentValues values = createContentValues(properties);

        if (getDb().update(Database.category.T, values, Database.category.id + "=" + key.getId(), null) != 1) {
            logInfo("Could not update category " + key.getId());
            return false;
        }
        mCategoryIdCache.onUpdate(properties);
        mCacheCategory.remove(key.getId());

        return true;
    }

    public boolean updateMediaItem(MediaKey key, MediaProperties properties) {
        ContentValues values = createContentValues(properties);

        if (getDb().update(Database.media.T, values, Database.media.id + "=" + key.getId(), null) != 1) {
            logInfo("Could not update media item " + key.getId());
            return false;
        }
        mMediaIdCache.onUpdate(properties);
        mCacheMedia.remove(key.getId());

        return true;
    }

    public boolean updateReference(ReferenceKey key, ReferenceProperties properties) {
        ContentValues values = createContentValues(properties);

        if (getDb().update(Database.reference.T, values, Database.reference.id + "=" + key.getId(), null) != 1) {
            logInfo("Could not update reference " + key.getId());
            return false;
        }
        mReferenceIdCache.onUpdate(properties);
        mCacheReference.remove(key.getId());

        return true;
    }

    private ContentValues createContentValues(MediaProperties properties) {
        ContentValues values = new ContentValues();
        values.put(Database.media.server_id, properties.getServerId());
        values.put(Database.media.is_publishable, properties.isPublishable());
        values.put(Database.media.mime_type, properties.getMimeType());
        values.put(Database.media.server_revision_id, properties.getServerRevisionId());
        values.put(Database.media.uri, properties.getURI().toString());
        return values;
    }

    private void deleteCategoriesFromActivity(ActivityKey key) {
        getDb().delete(Database.activity_data_category.T, Database.activity_data_category.activity_data_id + "=" + key.getId(), null);
    }

    private void deleteReferencesFromActivity(ActivityKey key) {
        getDb().delete(Database.activity_data_reference.T, Database.activity_data_reference.activity_data_id + "=" + key.getId(), null);
    }

    private void deleteMediaItemsFromActivity(ActivityKey key) {
        getDb().delete(Database.activity_data_media.T, Database.activity_data_media.activity_data_id + "=" + key.getId(), null);
    }

    private void addCategoriesToActivity(Long activityId, List<? extends Category> categories) {
        for (Category category : categories) {

            long categoryId = getOrCreateCategory(category);

            ContentValues associationEntry = new ContentValues();
            associationEntry.put(Database.activity_data_category.category_id, categoryId);
            associationEntry.put(Database.activity_data_category.activity_data_id, activityId);
            getDb().insert(Database.activity_data_category.T, null, associationEntry);
        }
    }

    private void addReferencesToActivity(Long activityId, List<? extends Reference> references) {
        for (Reference reference : references) {

            long referenceId = getOrCreateReference(reference);

            ContentValues associationEntry = new ContentValues();
            associationEntry.put(Database.activity_data_reference.reference_id, referenceId);
            associationEntry.put(Database.activity_data_reference.activity_data_id, activityId);
            getDb().insert(Database.activity_data_reference.T, null, associationEntry);
        }
    }

    private void addMediaItemsToActivity(Long activityId, List<? extends Media> mediaItems) {
        boolean first = true;
        for (Media media : mediaItems) {

            long mediaId = getOrCreateMediaItem(media);

            ContentValues associationEntry = new ContentValues();
            associationEntry.put(Database.activity_data_media.media_id, mediaId);
            associationEntry.put(Database.activity_data_media.activity_data_id, activityId);
            associationEntry.put(Database.activity_data_media.featured, first ? 1 : 0);
            getDb().insert(Database.activity_data_media.T, null, associationEntry);
            first = false;
        }
    }

    // todo: How much does DatabaseHelper.getOrCreateCategory and RemoteActivityRepoImpl.processServerObject overlap?
    public long getOrCreateCategory(CategoryProperties category) {
        long id = getLocalIdByServerId(category);
        if (id > 0) {
            return id;
        } else {
            return createCategory(category);
        }
    }

    public long getOrCreateMediaItem(MediaProperties properties) {
        long id = getLocalIdByServerId(properties);
        if (id > 0) {
            return id;
        } else {
            return createMediaItem(properties);
        }
    }

    public long getOrCreateReference(ReferenceProperties properties) {
        long id = getLocalIdByServerId(properties);
        if (id > 0) {
            return id;
        } else {
            return createReference(properties);
        }
    }

    public long getOrCreateUser(UserProperties user) {
        List<UserBean> userPojos = readUsers();
        for (UserBean userPojo : userPojos) {
            if (userPojo.getServerId() == user.getServerId()) {
                // Bingo! Reference already exists in local database.
                return userPojo.getId();
            }
        }
        return createUser(user);
    }

    private ContentValues createContentValues(ActivityProperties properties) {
        ContentValues values = new ContentValues();
        values.put(Database.activity.is_publishable, 0);
//        for (ActivityRevision revision : properties.getRevisions()) {
//            long activityDataId = addActivityData(owner, activityId, properties);

        String description = properties.getDescription();
        String descriptionNotes = properties.getDescriptionNotes();

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

//        ContentValues values = new ContentValues();
//        values.put(Database.activity_data.activity_id, activityId);
//        values.put(Database.activity_data.status, STATUS_NEW);
        values.put(Database.activity.server_id, properties.getServerId());
        values.put(Database.activity.server_revision_id, properties.getServerRevisionId());
        values.put(Database.activity.name, properties.getName());
//        final SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        values.put(Database.activity.datetime_published, properties.getDatePublished().getTime());
        values.put(Database.activity.datetime_created, properties.getDateCreated().getTime());
        values.put(Database.activity.descr_material, properties.getDescriptionMaterial());
        values.put(Database.activity.descr_introduction, properties.getDescriptionIntroduction());
        values.put(Database.activity.descr_prepare, properties.getDescriptionPreparation());
        values.put(Database.activity.descr_activity, description);
        values.put(Database.activity.descr_safety, properties.getDescriptionSafety());
        values.put(Database.activity.descr_notes, descriptionNotes);
        Range<Integer> ages = properties.getAges();
        if (ages != null) {
            values.put(Database.activity.age_min, ages.getMin());
            values.put(Database.activity.age_max, ages.getMax());
        } else {
            values.putNull(Database.activity.age_min);
            values.putNull(Database.activity.age_max);
        }
        Range<Integer> participants = properties.getParticipants();
        if (participants != null) {
            values.put(Database.activity.participants_min, participants.getMin());
            values.put(Database.activity.participants_max, participants.getMax());
        } else {
            values.putNull(Database.activity.participants_min);
            values.putNull(Database.activity.participants_max);
        }
        Range<Integer> time = properties.getTimeActivity();
        if (time != null) {
            values.put(Database.activity.time_min, time.getMin());
            values.put(Database.activity.time_max, time.getMax());
        } else {
            values.putNull(Database.activity.time_min);
            values.putNull(Database.activity.time_max);
        }
        values.put(Database.activity.featured, properties.isFeatured() ? 1 : 0);
        if (properties.getOwner() != null) {
            values.put(Database.activity.owner_id, properties.getOwner().getId());
        } else {
            values.putNull(Database.activity.owner_id);
        }
        if (properties.getFavouritesCount() != null) {
            values.put(Database.activity.favourite_count, properties.getFavouritesCount());
        } else {
            values.putNull(Database.activity.favourite_count);
        }
//        values.put(Database.activity_data.source_uri, properties.getSourceURI() != null ? properties.getSourceURI().toString() : null);
        return values;
    }

    private ContentValues createContentValues(CategoryProperties properties) {
        ContentValues values = new ContentValues();
        values.put(Database.category.server_id, properties.getServerId());
        values.put(Database.category.group_name, properties.getGroup());
        values.put(Database.category.name, properties.getName());
        values.putNull(Database.category.owner_id);
        values.put(Database.category.server_revision_id, properties.getServerRevisionId());
        values.put(Database.category.status, " ");
        // Add UUID only because data model requires the UUID column to be non-null and unique. The UUID is never used. SQLite does not support ALTER TABLE DROP COLUMN.
        values.put(Database.category.uuid, getUUIDBytes(UUID.randomUUID()).array());

        if (properties.getIconMediaKey() != null) {
            values.put(Database.category.icon_media_id, properties.getIconMediaKey().getId());
        } else {
            values.putNull(Database.category.icon_media_id);
        }
        return values;
    }

    private ByteBuffer getUUIDBytes(UUID uuid) {
        byte[] bytes = new byte[16];
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        bb.order(ByteOrder.LITTLE_ENDIAN);//) or ByteOrder.BIG_ENDIAN);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb;
    }

    private void addActivityDataMedia(long activityDataId, boolean isFeatured, Media media) {
        Cursor cursor = getDb().query(Database.media.T, new String[]{Database.media.id}, Database.media.uri + " = ?", new String[]{media.getURI().toString()}, null, null, null);
        long mediaId;
        if (cursor.moveToFirst()) {
            mediaId = cursor.getLong(0);
        } else {
            ContentValues refValues = new ContentValues();
//            refValues.put(Database.media.status, STATUS_NEW);
            refValues.put(Database.media.uri, media.getURI().toString());
            refValues.put(Database.media.is_publishable, media.isPublishable() ? 1 : 0);
            refValues.put(Database.media.server_id, media.getServerId());
            refValues.put(Database.media.mime_type, media.getMimeType());
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

    public List<? extends ActivityBean> readActivities(ActivityFilter filter) {
        QueryBuilder queryBuilder = new QueryBuilder();
        applyFilter(queryBuilder, filter);
        ArrayList<ActivityBean> activities = new ArrayList<ActivityBean>();
        Map<Long, ActivityBean> map = new HashMap<Long, ActivityBean>();
        ActivityDataCursor cursor = queryBuilder.query(getDb());
        while (cursor.moveToNext()) {
            long activityId = cursor.getId();
            if (!mCacheActivity.containsKey(activityId)) {
                ActivityBean revision = cursor.getActivityData();
                map.put(revision.getId(), revision);
                mCacheActivity.put(activityId, revision);
            }
            activities.add(mCacheActivity.get(activityId));
        }
        cursor.close();

        initActivitiesCategories(map);

        initActivitiesMedia(map);

        initActivitiesReferences(map);

        return activities;
    }

    private void applyFilter(QueryBuilder queryBuilder, ActivityFilter filter) {
        if (filter instanceof AndFilter) {
            AndFilter andFilter = (AndFilter) filter;
            for (ActivityFilter activityFilter : andFilter.getFilters()) {
                applyFilter(queryBuilder, activityFilter);
            }
        } else if (filter instanceof OrFilter) {
            OrFilter orFilter = (OrFilter) filter;
            for (ActivityFilter activityFilter : orFilter.getFilters()) {
                applyFilter(queryBuilder, activityFilter);
            }
        } else if (filter instanceof SQLActivityFilter) {
            ((SQLActivityFilter) filter).applyFilter(queryBuilder);
        } else {
//            IllegalArgumentException exception = new IllegalArgumentException("applyFilter does not support " + filter.getClass().getName());
//            logError(exception, "Unsupported filter");
//            throw exception;
            logInfo("applyFilter cannot use " + filter.getClass().getName());
        }
    }

    public List<CategoryBean> readCategories() {
        ArrayList<CategoryBean> categories = new ArrayList<CategoryBean>();
        CategoryCursor catCursor = new CategoryCursor(getDb());
        while (catCursor.moveToNext()) {
            long categoryId = catCursor.getId();
            if (!mCacheCategory.containsKey(categoryId)) {
                mCacheCategory.put(categoryId, catCursor.getCategory());
            }
            categories.add(mCacheCategory.get(categoryId));
        }
        catCursor.close();
        return categories;
    }

    public List<ReferenceBean> readReferences() {
        ArrayList<ReferenceBean> references = new ArrayList<ReferenceBean>();
        mCacheReference.clear();
        ReferenceCursor refCursor = new ReferenceCursor(getDb());
        while (refCursor.moveToNext()) {
            long referenceId = refCursor.getId();
            try {
                if (!mCacheReference.containsKey(referenceId)) {
                    mCacheReference.put(referenceId, refCursor.getReference());
                }
                references.add(mCacheReference.get(referenceId));
            } catch (URISyntaxException e) {
                LogUtil.e(DatabaseHelper.class.getName(), "Could not parse URI in database. Reference " + referenceId + " will not be accessible by client.", e);
            }
        }
        refCursor.close();
        return references;
    }

    public List<MediaBean> readMediaItems() {
        ArrayList<MediaBean> mediaItems = new ArrayList<MediaBean>();
        mCacheMedia.clear();
        MediaCursor medCursor = new MediaCursor(getDb());
        while (medCursor.moveToNext()) {
            long mediaId = medCursor.getId();
            try {
                if (!mCacheMedia.containsKey(mediaId)) {
                    mCacheMedia.put(mediaId, medCursor.getMedia());
                }
                mediaItems.add(mCacheMedia.get(mediaId));
            } catch (URISyntaxException e) {
                LogUtil.e(DatabaseHelper.class.getName(), "Could not parse URI in database. Media item " + mediaId + " will not be accessible by client.", e);
            }
        }
        medCursor.close();
        return mediaItems;
    }

    public List<UserBean> readUsers() {
        ArrayList<UserBean> users = new ArrayList<UserBean>();
        mCacheUser.clear();
        UserCursor userCursor = new UserCursor(getDb());
        while (userCursor.moveToNext()) {
            long userId = userCursor.getId();
            if (!mCacheUser.containsKey(userId)) {
                mCacheUser.put(userId, userCursor.getUser());
            }
            users.add(mCacheUser.get(userId));
        }
        userCursor.close();
        return users;
    }

    private void initActivitiesReferences(Map<Long, ActivityBean> revisions) {
        Set<Long> activityIds = revisions.keySet();
        ReferenceCursor refCursor = new ReferenceCursor(getDb(), activityIds);
        while (refCursor.moveToNext()) {
            try {
                long refId = refCursor.getId();
                if (!mCacheReference.containsKey(refId)) {
                    mCacheReference.put(refId, refCursor.getReference());
                }
                long activityDataId = refCursor.getLong(refCursor.getColumnIndex("activity_id"));
                revisions.get(activityDataId).getReferences().add(mCacheReference.get(refId));
            } catch (URISyntaxException e) {
                logError(e, "Invalid URI");
            }
        }
        refCursor.close();
    }

    private void initActivitiesMedia(Map<Long, ActivityBean> revisions) {
        Set<Long> activityIds = revisions.keySet();
        MediaCursor mediaCursor = new MediaCursor(getDb(), activityIds);
        while (mediaCursor.moveToNext()) {
            try {
                long mediaId = mediaCursor.getId();
                if (!mCacheMedia.containsKey(mediaId)) {
                    mCacheMedia.put(mediaId, mediaCursor.getMedia());
                }
                long activityDataId = mediaCursor.getLong(mediaCursor.getColumnIndex("activity_id"));
                revisions.get(activityDataId).getMediaItems().add(mCacheMedia.get(mediaId));
            } catch (URISyntaxException e) {
                logError(e, "Invalid URI");
            }
        }
        mediaCursor.close();
    }

    private void initActivitiesCategories(Map<Long, ActivityBean> revisions) {
        Set<Long> activityIds = revisions.keySet();
        CategoryCursor catCursor = new CategoryCursor(getDb(), activityIds);
        while (catCursor.moveToNext()) {
            long categoryId = catCursor.getId();
            if (!mCacheCategory.containsKey(categoryId)) {
                mCacheCategory.put(categoryId, catCursor.getCategory());
            }
            long activityDataId = catCursor.getLong(catCursor.getColumnIndex("activity_id"));
            revisions.get(activityDataId).getCategories().add(mCacheCategory.get(categoryId));
        }
        catCursor.close();
    }

    public Category readCategory(CategoryKey key) {
        if (!mCacheCategory.containsKey(key.getId())) {
            readCategories();
        }
        return mCacheCategory.get(key.getId());
    }

    public Media readMedia(MediaKey key) {
        if (key != null) {
            if (!mCacheMedia.containsKey(key.getId())) {
                readMediaItems();
            }
            return mCacheMedia.get(key.getId());
        } else {
            return null;
        }
    }

    public boolean isFavourite(ActivityKey activity, UserKey user) {
        try {
            Cursor cursor = getDb().query(Database.favourite_activity.T,
                    new String[]{Database.favourite_activity.activity_id}, "" +
                    Database.favourite_activity.user_id + " = " + user.getId() +
                    " AND "
                    + Database.favourite_activity.activity_id + " = " + activity.getId(),
                    null,
                    null,
                    null,
                    null);
            return cursor.moveToNext();
        } catch (Exception e) {
            logError(e, "Could not determine if user " + user.getId() + " has activity " + activity.getId() + " as a favourite");
            return false;
        }
    }

    public void setFavourite(ActivityKey activity, UserKey user) {
        ContentValues values = new ContentValues();
        values.put(Database.favourite_activity.user_id, user.getId());
        values.put(Database.favourite_activity.activity_id, activity.getId());
        getDb().insert(Database.favourite_activity.T, null, values);
    }

    public void unsetFavourite(ActivityKey activity, UserKey user) {
        getDb().delete(Database.favourite_activity.T, "" +
                Database.favourite_activity.user_id + " = " + user.getId() +
                " AND "
                + Database.favourite_activity.activity_id + " = " + activity.getId(),
                null);
    }

    public ArrayList<SearchHistoryBean> readSearchHistory(UserKey user, boolean descendingOrder, int limit, boolean onlyUnique) {
        Collection<SearchHistoryBean> items = onlyUnique ? new LinkedHashSet<SearchHistoryBean>() : new ArrayList<SearchHistoryBean>();
        try {
            SearchHistoryCursor cursor = new SearchHistoryCursor(getDb(), user, descendingOrder);
            while (cursor.moveToNext() && (limit == 0 || items.size() < limit)) {
                long id = cursor.getId();
                if (!mBannedSearchHistoryIds.contains(id)) {
                    try {
                        SearchHistoryBean item = cursor.getHistoryItem();
                        items.add(item);
                    } catch (Throwable e) {
                        logError(e, "Could not read search history");
                        mBannedSearchHistoryIds.add(id);
                    }
                } else {
                    logDebug("Ignoring banned search history item " + id);
                }
            }
            deleteBannedSearchHistoryItems();
        } catch (Exception e) {
            logError(e, "Could not read search history");
        }
        return items instanceof ArrayList ? (ArrayList<SearchHistoryBean>) items : new ArrayList(items);
    }

    private void deleteBannedSearchHistoryItems() {
        if (!mBannedSearchHistoryIds.isEmpty()) {
            getDb().beginTransaction();
            Iterator<Long> iterator = mBannedSearchHistoryIds.iterator();
            while (iterator.hasNext()) {
                Long next = iterator.next();
                if (getDb().delete(Database.history.T, Database.history.id + " = " + next.toString(), null) == 1) {
                    iterator.remove();
                    logDebug("Deleted banned search history id " + next.toString());
                }
            }
            getDb().setTransactionSuccessful();
            getDb().endTransaction();
        }
    }

    public void createSearchHistoryItem(HistoryProperties<SearchHistoryData> properties, UserKey userKey) throws IOException {
        ContentValues values = new ContentValues();
        values.put(Database.history.data, serializeObject(properties.getData()));
        values.put(Database.history.user_id, userKey.getId());
        values.put(Database.history.type, String.valueOf(HistoryType.SEARCH.getDatabaseValue()));

        getDb().insert(Database.history.T, null, values);
    }

    private byte[] serializeObject(Object data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(baos);
        stream.writeObject(data);
        stream.close();
        baos.close();
        return baos.toByteArray();
    }

    public LocalObjectRefreshness getLocalFreshness(ActivityBean serverObject) {
        return mActivityIdCache.getLocalObjectFreshness(serverObject);
    }

    public LocalObjectRefreshness getLocalFreshness(CategoryBean serverObject) {
        return mCategoryIdCache.getLocalObjectFreshness(serverObject);
    }

    public LocalObjectRefreshness getLocalFreshness(MediaBean serverObject) {
        return mMediaIdCache.getLocalObjectFreshness(serverObject);
    }

    public LocalObjectRefreshness getLocalFreshness(ReferenceBean serverObject) {
        return mReferenceIdCache.getLocalObjectFreshness(serverObject);
    }

    public long getLocalIdByServerId(ActivityProperties serverObjectIdentifier) {
        return mActivityIdCache.getLocalIdByServerId(serverObjectIdentifier);
    }

    public long getLocalIdByServerId(CategoryProperties serverObjectIdentifier) {
        return mCategoryIdCache.getLocalIdByServerId(serverObjectIdentifier);
    }

    public long getLocalIdByServerId(MediaProperties serverObjectIdentifier) {
        return mMediaIdCache.getLocalIdByServerId(serverObjectIdentifier);
    }

    public long getLocalIdByServerId(ReferenceProperties serverObjectIdentifier) {
        return mReferenceIdCache.getLocalIdByServerId(serverObjectIdentifier);
    }

    public Set<ActivityKey> getFavourites(UserKey userKey) {
        Set<ActivityKey> result = new HashSet<ActivityKey>();
        Cursor cursor = getDb().query(Database.favourite_activity.T,
                new String[]{Database.favourite_activity.activity_id}, "" +
                Database.favourite_activity.user_id + " = " + userKey.getId(),
                null,
                null,
                null,
                null);
        while (cursor.moveToNext()) {
            result.add(new ObjectIdentifierBean(cursor.getLong(cursor.getColumnIndex(Database.favourite_activity.activity_id))));
        }
        return result;
    }

    public boolean updateUserAPIKey(String apiKey, UserKey userKey) {
        logInfo("Setting API key for user " + userKey.getId() + " to " + apiKey + " in database.");

        ContentValues values = new ContentValues();
        values.put(Database.user.api_key, apiKey);
        int rowsUpdated = getDb().update(Database.user.T, values, Database.user.id + "=" + userKey.getId(), null);

        mCacheUser.remove(userKey.getId());

        return rowsUpdated == 1;
    }

    public User readUser(UserKey key) {
        if (!mCacheUser.containsKey(key.getId())) {
            readUsers();
        }
        return mCacheUser.get(key.getId());
    }

}
