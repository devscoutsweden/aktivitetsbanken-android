package se.devscout.android.model;

import se.devscout.server.api.model.SystemMessage;

import java.util.Date;

public class SystemMessageBean extends SystemMessagePropertiesBean implements SystemMessage {
    private Long mId;

    public SystemMessageBean(Long id, long serverId, long serverRevisionId, String key, Date validFrom, Date validTo, String value) {
        super(serverId, serverRevisionId, key, validFrom, validTo, value);
        mId = id;
    }

    @Override
    public Long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }
}
