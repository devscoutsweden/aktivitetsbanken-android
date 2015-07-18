package se.devscout.server.api.model;

public interface UserProfile extends User {
    String getRole();
    String[] getRolePermissions();
}
