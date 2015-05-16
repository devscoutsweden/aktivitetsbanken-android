package se.devscout.android.model;

import se.devscout.server.api.UserProfile;

public class UserProfileBean extends UserBean implements UserProfile {
//    private String[] mAPIKeys;
    private String[] mRolePermissions;
    private String mRole;

    public UserProfileBean(String displayName, String apiKey, Long id, long serverId, long serverRevisionId, String emailAddress) {
        super(displayName, apiKey, id, serverId, serverRevisionId, emailAddress);
    }

    @Override
    public String getRole() {
        return mRole;
    }

    @Override
    public String[] getRolePermissions() {
        return mRolePermissions;
    }

//    @Override
//    public String[] getAPIKeys() {
//        return mAPIKeys;
//    }

//    public void setAPIKeys(String[] APIKeys) {
//        mAPIKeys = APIKeys;
//    }

    public void setRole(String role) {
        mRole = role;
    }

    public void setRolePermissions(String[] rolePermissions) {
        mRolePermissions = rolePermissions;
    }
}
