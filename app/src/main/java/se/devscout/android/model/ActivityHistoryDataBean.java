package se.devscout.android.model;

public class ActivityHistoryDataBean extends ObjectIdentifierBean implements ActivityHistoryData {
    public ActivityHistoryDataBean(Long id) {
        super(id);
    }

    public ActivityHistoryDataBean(ObjectIdentifier identifier) {
        super(identifier);
    }
}
