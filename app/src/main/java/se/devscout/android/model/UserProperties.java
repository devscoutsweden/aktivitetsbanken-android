package se.devscout.android.model;

public interface UserProperties extends ServerObjectProperties {
    String getName();

    String getDisplayName();

    String getAPIKey();

    String getEmailAddress();

//    boolean isEmailAddressVerified();

//    byte[] getPasswordHash();

//    HashAlgorithm getPasswordHashAlgorithm();

//    String getPasswordHashAlgorithmParameter();
}
