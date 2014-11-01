package se.devscout.android.model.repo.sql.cache;

public class IdCacheEntry {
    private final long[] mCompareValues;
    private long mId;
    private long mServerId;

    public IdCacheEntry(long id, long serverId, long[] values) {
        mId = id;
        mServerId = serverId;
        mCompareValues = values;
    }

    public long getId() {
        return mId;
    }

    public long getServerId() {
        return mServerId;
    }

    public boolean isAdditionalValuesListIdentical(IdCacheEntry idCacheEntry) {
        for (int i = 0; i < mCompareValues.length; i++) {
            long value = mCompareValues[i];
            if (value != idCacheEntry.mCompareValues[i]) {
                return false;
            }
        }
        return true;
    }
}
