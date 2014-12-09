package se.devscout.android.view;

import java.io.Serializable;
import java.net.URI;

public class AsyncImageBean implements Serializable {
    private String mName;
    private URI[] mURIs;

    public AsyncImageBean(String name, URI... uris) {
        mName = name;
        setURIs(uris);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public URI[] getURIs() {
        return mURIs;
    }

    public void setURIs(URI[] uris) {
        if (uris != null && !(uris.length == 1 && uris[0] == null)) {
            mURIs = uris;
        } else {
            mURIs = null;
        }
    }
}
