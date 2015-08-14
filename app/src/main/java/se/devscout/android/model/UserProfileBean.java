package se.devscout.android.model;

public class UserProfileBean extends UserBean implements UserProfile {
    private String[] mRolePermissions = {};

    public UserProfileBean(String displayName, String apiKey, Long id, long serverId, long serverRevisionId, String emailAddress, String role) {
        super(displayName, apiKey, id, serverId, serverRevisionId, emailAddress, role);
    }

    @Override
    public String[] getRolePermissions() {
        return mRolePermissions;
    }

    public void setRolePermissions(String[] rolePermissions) {
        mRolePermissions = rolePermissions;
    }
}
