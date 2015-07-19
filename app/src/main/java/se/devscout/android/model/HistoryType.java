package se.devscout.android.model;

public enum HistoryType {
    SEARCH('s'),
    ACTIVITY('a');

    private final char mDatabaseValue;

    private HistoryType(char databaseValue) {
        mDatabaseValue = databaseValue;
    }

    public char getDatabaseValue() {
        return mDatabaseValue;
    }
}
