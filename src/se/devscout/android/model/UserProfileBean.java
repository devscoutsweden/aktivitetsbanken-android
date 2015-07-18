package se.devscout.android.model;

public class UserProfileBean extends UserBean implements UserProfile {
    private String[] mRolePermissions = {};
    private String mRole = "unknown";

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

    public void setRole(String role) {
        mRole = role;
    }

    public void setRolePermissions(String[] rolePermissions) {
        mRolePermissions = rolePermissions;
    }
}
