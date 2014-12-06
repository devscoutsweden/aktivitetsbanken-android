package se.devscout.android.view;

import se.devscout.server.api.model.MediaProperties;

import java.io.Serializable;

public class AsyncImageBean implements Serializable {
    private String mName;
    private MediaProperties mMedia;

    public AsyncImageBean(MediaProperties media, String name) {
        mMedia = media;
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public MediaProperties getMedia() {
        return mMedia;
    }

    public void setMedia(MediaProperties media) {
        mMedia = media;
    }
}
