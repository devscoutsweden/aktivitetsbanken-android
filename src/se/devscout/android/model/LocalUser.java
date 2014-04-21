package se.devscout.android.model;

import se.devscout.server.api.model.HashAlgorithm;
import se.devscout.server.api.model.User;

import java.io.Serializable;

public class LocalUser extends LocalObjectIdentifier implements User, Serializable {
    private String mName;
    private String mDisplayName;
    private String mEmailAddress;
    private boolean mEmailAddressVerified;
    private HashAlgorithm mPasswordHashAlgorithm;
    private String mPasswordHashAlgorithmParameter;

    public LocalUser() {
    }

    public LocalUser(Integer id) {
        super(id);
    }

    public LocalUser(Integer id, String name) {
        super(id);
        mName = name;
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

    @Override
    public boolean isEmailAddressVerified() {
        return mEmailAddressVerified;
    }

    @Override
    public byte[] getPasswordHash() {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HashAlgorithm getPasswordHashAlgorithm() {
        return mPasswordHashAlgorithm;
    }

    @Override
    public String getPasswordHashAlgorithmParameter() {
        return mPasswordHashAlgorithmParameter;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public void setEmailAddress(String emailAddress) {
        mEmailAddress = emailAddress;
    }

    public void setEmailAddressVerified(boolean emailAddressVerified) {
        mEmailAddressVerified = emailAddressVerified;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setPasswordHashAlgorithm(HashAlgorithm passwordHashAlgorithm) {
        mPasswordHashAlgorithm = passwordHashAlgorithm;
    }

    public void setPasswordHashAlgorithmParameter(String passwordHashAlgorithmParameter) {
        mPasswordHashAlgorithmParameter = passwordHashAlgorithmParameter;
    }
}
