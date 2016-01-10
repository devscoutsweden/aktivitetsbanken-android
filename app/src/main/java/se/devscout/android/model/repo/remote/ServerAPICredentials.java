package se.devscout.android.model.repo.remote;

public class ServerAPICredentials {
    static final String API_TOKEN_TYPE_API_KEY = "ApiKey";
    public static final String API_TOKEN_TYPE_GOOGLE = "Google";
    private String mType;
    private String mToken;

    public ServerAPICredentials(String type, String token) {
        mToken = token;
        mType = type;
    }

    public static ServerAPICredentials fromString(String raw) {
        return new ServerAPICredentials(raw.substring(0, raw.indexOf(';')), raw.substring(raw.indexOf(';') + 1));
    }

    @Override
    public String toString() {
        return mType + ";" + mToken;
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
        if (!(o instanceof ServerAPICredentials)) return false;

        ServerAPICredentials that = (ServerAPICredentials) o;

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
