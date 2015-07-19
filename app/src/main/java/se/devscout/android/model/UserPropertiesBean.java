package se.devscout.android.model;

public class UserPropertiesBean extends ServerObjectPropertiesBean implements UserProperties {
    protected String mName;
    private String mAPIKey;
    private String mDisplayName;
    private String mEmailAddress;

    public UserPropertiesBean() {
    }

    public UserPropertiesBean(String displayName, String apiKey, long serverId, long serverRevisionId, boolean publishable, String emailAddress) {
        super(publishable, serverId, serverRevisionId);
        mDisplayName = displayName;
        mAPIKey = apiKey;
        mEmailAddress = emailAddress;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String getDisplayName() {
        return mDisplayName;
    }

    @Override
    public String getEmailAddress() {
        return mEmailAddress;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public void setEmailAddress(String emailAddress) {
        mEmailAddress = emailAddress;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public String getAPIKey() {
        return mAPIKey;
    }

    public void setAPIKey(String APIKey) {
        mAPIKey = APIKey;
    }
}
