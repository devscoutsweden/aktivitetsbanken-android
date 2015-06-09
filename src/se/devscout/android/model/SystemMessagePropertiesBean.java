package se.devscout.android.model;

import se.devscout.server.api.model.SystemMessageProperties;

import java.util.Date;

public class SystemMessagePropertiesBean extends ServerObjectPropertiesBean implements SystemMessageProperties {
    private String mKey;
    private String mValue;
    private Date mValidFrom;
    private Date mValidTo;

    public SystemMessagePropertiesBean(long serverId, long serverRevisionId, String key, Date validFrom, Date validTo, String value) {
        super(false, serverId, serverRevisionId);
        mKey = key;
        mValidFrom = validFrom;
        mValidTo = validTo;
        mValue = value;
    }

    @Override
    public String getKey() {
        return mKey;
    }

    @Override
    public String getValue() {
        return mValue;
    }

    @Override
    public Date getValidFrom() {
        return mValidFrom;
    }

    @Override
    public Date getValidTo() {
        return mValidTo;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public void setValidFrom(Date validFrom) {
        mValidFrom = validFrom;
    }

    public void setValidTo(Date validTo) {
        mValidTo = validTo;
    }

    public void setValue(String value) {
        mValue = value;
    }
}
