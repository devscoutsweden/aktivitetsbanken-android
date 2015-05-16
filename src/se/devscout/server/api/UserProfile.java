package se.devscout.server.api;

import se.devscout.server.api.model.User;

public interface UserProfile extends User {
    String getRole();
    String[] getRolePermissions();
//    String[] getAPIKeys();
}
