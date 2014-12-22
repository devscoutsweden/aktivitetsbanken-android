package se.devscout.android.model.repo.remote;

class Credentials {
    private String mType;
    private String mToken;

    Credentials(String token, String type) {
        mToken = token;
        mType = type;
    }

    public String getToken() {
        return mToken;
    }

    public String getType() {
        return mType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Credentials)) return false;

        Credentials that = (Credentials) o;

        if (!mToken.equals(that.mToken)) return false;
        if (mType != that.mType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mType.hashCode();
        result = 31 * result + mToken.hashCode();
        return result;
    }
}
