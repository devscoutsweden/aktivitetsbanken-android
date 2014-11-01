package se.devscout.android.model.repo.sql.cache;

import android.database.Cursor;
import se.devscout.android.model.repo.sql.DatabaseHelper;
import se.devscout.android.model.repo.sql.LocalObjectRefreshness;
import se.devscout.server.api.model.ServerObjectIdentifier;
import se.devscout.server.api.model.ServerObjectProperties;
import se.devscout.server.api.model.SynchronizedServerObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Keeps track of key object values, such as primary key value in local
 * database and id value on the server. The purpose is to use this
 * information to determine if the local information is stale and needs to
 * be updated based on the most recent information from the server.
 * <p/>
 * The cache is also used to map identifiers of objects recieved from the
 * server, which uses the server's own object identifiers, to the object
 * identifiers used by within the app's database.
 * <p/>
 * When determining is local data is stale, start by comparing the local id
 * to the server id. If these are equal, the class supports checking an
 * additional list object properties to see if they might differ. This is
 * used for versioned objects (where the primary key stays the same between
 * edits but a revision counter gets incremented) and for objects which can
 * be "favourited" (where changes to the "favourite counter" affects neither
 * id nor server id).
 *
 * @param <T>
 */
public abstract class ServerObjectIdCache<T extends SynchronizedServerObject, P extends ServerObjectProperties> {
    private List<IdCacheEntry> mEntries = null;
    private String mIdColumnName;
    private String mServerIdColumnName;
    private String[] mCompareColumnNames;
    private String mTable;
    private DatabaseHelper mDatabaseHelper;

    public ServerObjectIdCache(DatabaseHelper databaseHelper, String table, String idColumnName, String serverIdColumnName, String... compareColumnNames) {
        mDatabaseHelper = databaseHelper;
        mIdColumnName = idColumnName;
        mServerIdColumnName = serverIdColumnName;
        mCompareColumnNames = compareColumnNames;
        mTable = table;
    }

    public void invalidate() {
        mEntries = null;
    }

    private List<IdCacheEntry> getEntries() {
        if (mEntries == null) {
            mEntries = new ArrayList<IdCacheEntry>();
            String[] columnNames = Arrays.copyOf(mCompareColumnNames, mCompareColumnNames.length + 2);
            columnNames[columnNames.length - 1] = mIdColumnName;
            columnNames[columnNames.length - 2] = mServerIdColumnName;
            Cursor localIdsQuery = mDatabaseHelper.getDb().query(mTable, columnNames, null, null, null, null, null);
            while (localIdsQuery.moveToNext()) {
                long[] values = new long[mCompareColumnNames.length];
                for (int i = 0; i < mCompareColumnNames.length; i++) {
                    String columnName = mCompareColumnNames[i];
                    values[i] = localIdsQuery.getLong(localIdsQuery.getColumnIndex(columnName));
                }
                IdCacheEntry entry = new IdCacheEntry(localIdsQuery.getInt(localIdsQuery.getColumnIndex(mIdColumnName)), localIdsQuery.getInt(localIdsQuery.getColumnIndex(mServerIdColumnName)), values);
                mEntries.add(entry);
            }
        }
        return mEntries;
    }

    public LocalObjectRefreshness getLocalObjectFreshness(T serverObject) {
        IdCacheEntry entry = getEntryByServerId(serverObject.getServerId());
        if (entry != null) {
            // Object is cached
            if (!isAdditionalValuesListIdentical(entry, serverObject)) {
                // Incoming data is newer than cached data
                return LocalObjectRefreshness.LOCAL_IS_OLD;
            } else {
                // No need to do anything
                return LocalObjectRefreshness.LOCAL_IS_UP_TO_DATE;
            }
        } else {
            // Incoming data is a new (non-cached) activity. Add it to the local database.
            return LocalObjectRefreshness.LOCAL_IS_MISSING;
        }
    }

    public IdCacheEntry getEntryByServerId(long id) {
        for (IdCacheEntry entry : getEntries()) {
            if (entry.getServerId() == id) {
                return entry;
            }
        }
        return null;
    }

    public long getLocalIdByServerId(ServerObjectIdentifier serverObjectIdentifier) {
        IdCacheEntry entry = getEntryByServerId(serverObjectIdentifier.getServerId());
        return entry != null ? entry.getId() : -1;
    }

    public boolean isAdditionalValuesListIdentical(IdCacheEntry entry, T entry2) {
        return entry.isAdditionalValuesListIdentical(createIdCacheEntry(entry2));
    }

    public void addEntry(T entry) {
        mEntries.add(createIdCacheEntry(entry));
    }

    protected abstract T createObject(long id, P properties);

    protected abstract IdCacheEntry createIdCacheEntry(T entry);

    public void onInsert(long id, P properties) {
        if (properties.getServerId() != 0) {
            addEntry(id, properties);
        } else {
            invalidate();
        }
    }

    private void addEntry(long id, P properties) {
        IdCacheEntry entry = getEntryByServerId(properties.getServerId());
        if (entry != null) {
            //todo: When will this EVER happen!?
            mEntries.remove(entry);
        }
        addEntry(createObject(id, properties));
    }

    public void onUpdate(P properties) {
        if (properties.getServerId() != 0) {
            updateEntry(properties);
        } else {
            invalidate();
        }
    }

    private void updateEntry(P properties) {
        IdCacheEntry entry = getEntryByServerId(properties.getServerId());
        long id = entry.getId();
        if (entry != null) {
            mEntries.remove(entry);
        }
        addEntry(createObject(id, properties));
    }
}
