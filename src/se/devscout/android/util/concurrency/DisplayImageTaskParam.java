package se.devscout.android.util.concurrency;

import android.widget.ImageView;

import java.net.URI;

public class DisplayImageTaskParam {
    private final URI[] mURIs;
    private final int mMaxFileSize;
    private final ImageView mImageView;

    public DisplayImageTaskParam(ImageView imageView, int maxFileSize, URI[] URIs) {
        mImageView = imageView;
        mMaxFileSize = maxFileSize;
        mURIs = URIs;
    }

    public URI[] getURIs() {
        return mURIs;
    }

    public int getMaxFileSize() {
        return mMaxFileSize;
    }

    public ImageView getImageView() {
        return mImageView;
    }

}
