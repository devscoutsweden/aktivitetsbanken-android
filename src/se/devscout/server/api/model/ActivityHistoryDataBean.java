package se.devscout.server.api.model;

import se.devscout.android.model.ObjectIdentifierBean;

public class ActivityHistoryDataBean extends ObjectIdentifierBean implements ActivityHistoryData {
    public ActivityHistoryDataBean(Long id) {
        super(id);
    }

    public ActivityHistoryDataBean(ObjectIdentifier identifier) {
        super(identifier);
    }
}
