package se.devscout.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import se.devscout.android.R;

public class AsyncThumbnailImageView extends AsyncImageView {
    public AsyncThumbnailImageView(Context context) {
        super(context);
    }

    public AsyncThumbnailImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AsyncThumbnailImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void inflate(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.async_thumbnail_image, this, true);
    }

    @Override
    protected void onBitmapResponse(Bitmap response, ImageView imageView) {
//        findViewById(R.id.asyncImageProgressBar).setVisibility(View.GONE);
        findViewById(R.id.asyncThumbImageError).setVisibility(View.GONE);
        setImageAndFadeIn(response, imageView);
    }

    @Override
    protected void onExceptionResponse(Exception response, ImageView imageView) {
//        findViewById(R.id.asyncImageProgressBar).setVisibility(View.GONE);
        findViewById(R.id.asyncThumbImageError).setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
    }

    @Override
    protected void onInitImage(AsyncImageBean properties) {
        findViewById(R.id.asyncThumbImageError).setVisibility(View.GONE);
/*
        if (properties != null && properties.getURIs() != null) {
            findViewById(R.id.asyncImageProgressBar).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.asyncImageProgressBar).setVisibility(View.GONE);
        }
*/
    }

    public void setImage(int imageResource) {
//        findViewById(R.id.asyncImageProgressBar).setVisibility(View.GONE);
        findViewById(R.id.asyncThumbImageError).setVisibility(View.GONE);
        ImageView asyncImageView = (ImageView) findViewById(R.id.asyncImageImageView);
        asyncImageView.setVisibility(View.VISIBLE);
        asyncImageView.setImageResource(imageResource);
    }
}
