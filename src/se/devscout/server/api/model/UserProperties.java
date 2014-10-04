package se.devscout.server.api.model;

public interface UserProperties extends ServerObjectProperties {
    String getName();

    String getDisplayName();

    String getEmailAddress();

    boolean isEmailAddressVerified();

    byte[] getPasswordHash();

    HashAlgorithm getPasswordHashAlgorithm();

    String getPasswordHashAlgorithmParameter();
}
