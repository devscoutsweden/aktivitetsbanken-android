package se.devscout.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.ScoutTypeFace;
import se.devscout.android.util.concurrency.BackgroundTask;
import se.devscout.android.util.concurrency.BackgroundTasksHandlerThread;
import se.devscout.android.util.concurrency.DisplayImageTaskParam;
import se.devscout.android.util.http.ContentTooLongException;

import java.net.URI;
import java.util.Arrays;

public class AsyncImageView extends FrameLayout {

    public AsyncImageView(Context context) {
        super(context);
        inflate(context);
        init();
    }

    public AsyncImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context);
        init();
    }

    public AsyncImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context);
        init();
    }

    protected void inflate(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.async_image, this, true);
    }

    public void init() {
        setImage(null, null);
        setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    public void setImage(AsyncImageBean properties, BackgroundTasksHandlerThread backgroundTasksThread) {
        URI[] imageURIs = properties != null && properties.getURIs() != null && properties.getURIs().length > 0 ? properties.getURIs() : null;
        onInitImage(properties);
        ImageView asyncImageView = (ImageView) findViewById(R.id.asyncImageImageView);
        asyncImageView.setVisibility(View.GONE);
        if (imageURIs != null) {
            asyncImageView.setTag(R.id.imageViewUri, imageURIs);
            LogUtil.d(AsyncImageView.class.getName(), imageURIs + " should be loaded into " + asyncImageView.toString());
            // TODO: Make length limit customizable using attribute
            int limitLarge = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("server_download_limit_large", "50")) * 1000;
            backgroundTasksThread.addListener(createTaskListener(imageURIs));
            backgroundTasksThread.queueGetMediaResource(asyncImageView, imageURIs, limitLarge);
            backgroundTasksThread.queueCleanCache();
        }
    }

    protected void onInitImage(AsyncImageBean properties) {
        URI[] imageURIs = properties != null && properties.getURIs() != null && properties.getURIs().length > 0 ? properties.getURIs() : null;
        findViewById(R.id.asyncImageErrorLayout).setVisibility(View.GONE);
        findViewById(R.id.asyncImageErrorRetryButton).setVisibility(View.GONE);
        if (imageURIs != null) {
            findViewById(R.id.asyncImageProgressLayout).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.asyncImageProgressLayout).setVisibility(View.GONE);
        }
        String title = properties != null ? properties.getName() : null;
        TextView textView = (TextView) findViewById(R.id.asyncImageOverlayLabel);
        if (title != null && title.length() > 0) {
            textView.setTypeface(ScoutTypeFace.getInstance(getContext()).getLight());
            textView.setText(title);
            textView.setVisibility(VISIBLE);
        } else {
            textView.setVisibility(GONE);
        }
    }

    private BackgroundTasksHandlerThread.Listener createTaskListener(final URI[] requestedURIs) {
        return new BackgroundTasksHandlerThread.Listener() {
            @Override
            public BackgroundTasksHandlerThread.ListenerAction onDone(Object parameter, Object response, BackgroundTask task) {
                if (task == BackgroundTask.DISPLAY_IMAGE && parameter instanceof DisplayImageTaskParam) {
                    DisplayImageTaskParam displayImageTaskParam = (DisplayImageTaskParam) parameter;
                    ImageView imageView = displayImageTaskParam.getImageView();
                    URI[] eventURIs = displayImageTaskParam.getURIs();
                    if (Arrays.equals(eventURIs, (URI[]) imageView.getTag(R.id.imageViewUri))) {
                        if (response instanceof Bitmap) {
                            onBitmapResponse((Bitmap) response, imageView);
                        } else if (response instanceof Exception) {
                            onExceptionResponse((Exception) response, imageView);
                        } else {
                            LogUtil.e(AsyncImageView.class.getName(), "Response is neither Bitmap nor Exception");
                        }
                    } else {
                        LogUtil.d(AsyncImageView.class.getName(), "Image has been loaded but the image view has been recycled and is now used for another image.");
                    }
                    return Arrays.equals(requestedURIs, eventURIs) ? BackgroundTasksHandlerThread.ListenerAction.REMOVE : BackgroundTasksHandlerThread.ListenerAction.KEEP;
                }
                return BackgroundTasksHandlerThread.ListenerAction.KEEP;
            }
        };
    }

    protected void onBitmapResponse(Bitmap response, ImageView imageView) {
        findViewById(R.id.asyncImageProgressLayout).setVisibility(View.GONE);
        findViewById(R.id.asyncImageErrorLayout).setVisibility(View.GONE);

        setImageAndFadeIn(response, imageView);
    }

    protected void onExceptionResponse(Exception response, ImageView imageView) {
        findViewById(R.id.asyncImageProgressLayout).setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);

        findViewById(R.id.asyncImageErrorLayout).setVisibility(View.VISIBLE);
        TextView messageText = (TextView) findViewById(R.id.asyncImageErrorMessage);
        TextView detailsText = (TextView) findViewById(R.id.asyncImageErrorDetails);
        if (response instanceof ContentTooLongException) {
            ContentTooLongException headerException = (ContentTooLongException) response;
            messageText.setText(R.string.asyncImageTooLarge);
            detailsText.setText(getContext().getString(R.string.asyncImageTooLargeDetails, headerException.getLength() / 1000, headerException.getMaxLength() / 1000));
            detailsText.setVisibility(View.VISIBLE);
        } else {
            messageText.setText(R.string.asyncImageError);
            detailsText.setVisibility(View.GONE);
        }
    }

    public void setImage(int imageResource) {
        findViewById(R.id.asyncImageErrorLayout).setVisibility(View.GONE);
        findViewById(R.id.asyncImageErrorRetryButton).setVisibility(View.GONE);
        findViewById(R.id.asyncImageProgressLayout).setVisibility(View.GONE);
        ImageView asyncImageView = (ImageView) findViewById(R.id.asyncImageImageView);
        asyncImageView.setVisibility(View.VISIBLE);
        asyncImageView.setImageResource(imageResource);
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        ((ImageView) findViewById(R.id.asyncImageImageView)).setScaleType(scaleType);
    }

    public ImageView.ScaleType getScaleType() {
        return ((ImageView) findViewById(R.id.asyncImageImageView)).getScaleType();
    }

    protected void setImageAndFadeIn(Bitmap response, ImageView imageView) {
        imageView.setAlpha(0.0f);
        imageView.setVisibility(VISIBLE);
        imageView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200).alpha(1.0f);
        imageView.setImageBitmap(response);
    }
}
