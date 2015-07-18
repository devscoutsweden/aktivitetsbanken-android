package se.devscout.android.model;

public interface UserProfile extends User {
    String getRole();
    String[] getRolePermissions();
}
