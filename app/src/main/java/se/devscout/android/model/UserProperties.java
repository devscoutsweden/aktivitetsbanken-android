package se.devscout.android.model;

public interface UserProperties extends ServerObjectProperties {
    String USER_NAME_ANONYMOUS = "Anonymous";
    String ROLE_ADMINISTRATOR = "administrator";

    String getName();

    String getDisplayName();

    String getAPIKey();

    String getEmailAddress();

    String getRole();

//    boolean isEmailAddressVerified();

//    byte[] getPasswordHash();

//    HashAlgorithm getPasswordHashAlgorithm();

//    String getPasswordHashAlgorithmParameter();
}
