package se.devscout.android.util.concurrency;

import android.widget.ImageView;

import java.net.URI;

public class DisplayImageTaskParam {
    private URI[] mURIs;
    private int mMaxFileSize;
    private ImageView mImageView;

    public DisplayImageTaskParam(ImageView imageView, int maxFileSize, URI[] URIs) {
        mImageView = imageView;
        mMaxFileSize = maxFileSize;
        mURIs = URIs;
    }

    public URI[] getURIs() {
        return mURIs;
    }

    public void setURIs(URI[] URIs) {
        mURIs = URIs;
    }

    public int getMaxFileSize() {
        return mMaxFileSize;
    }

    public void setMaxFileSize(int maxFileSize) {
        mMaxFileSize = maxFileSize;
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public void setImageView(ImageView imageView) {
        mImageView = imageView;
    }
}
