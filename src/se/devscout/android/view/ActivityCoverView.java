package se.devscout.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.concurrency.BackgroundTask;
import se.devscout.android.util.concurrency.BackgroundTasksHandlerThread;
import se.devscout.android.util.http.ContentTooLongException;
import se.devscout.server.api.model.Media;

import java.net.URI;

//TODO: Rename to something like AsyncImageView
public class ActivityCoverView extends FrameLayout {

    public ActivityCoverView(Context context) {
        super(context);
        inflate(context);
        init((SingleFragmentActivity) context);
    }

    public ActivityCoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context);
        init((SingleFragmentActivity) context);
    }

    public ActivityCoverView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context);
        init((SingleFragmentActivity) context);
    }

    private void inflate(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        //TODO: Rename to something like R.layout.async_image
        inflater.inflate(R.layout.activity_cover, this, true);
    }

    public void init(SingleFragmentActivity activity) {
        init(null, "Image", activity, -1, ImageView.ScaleType.CENTER_CROP);
    }

    public void init(Media coverMedia, String title, SingleFragmentActivity activity, int imageSize) {
        init(coverMedia, title, activity, imageSize, ImageView.ScaleType.CENTER_CROP);
    }

    public void init(Media coverMedia, String title, SingleFragmentActivity activity, int imageSize, ImageView.ScaleType imageScaleType) {
        initImage(coverMedia, activity, imageSize, imageScaleType);
        initOverlay(title);
    }

    private void initImage(Media coverMedia, SingleFragmentActivity activity, int imageSize, ImageView.ScaleType imageScaleType) {
        findViewById(R.id.asyncImageErrorLayout).setVisibility(View.GONE);
        findViewById(R.id.asyncImageErrorRetryButton).setVisibility(View.GONE);
        ImageView activityCoverImage = (ImageView) findViewById(R.id.activityCoverImage);
        activityCoverImage.setVisibility(View.GONE);
        if (coverMedia != null && imageSize > 0) {

            URI coverImageURI = ActivityBankFactory.getInstance(activity).getMediaItemURI(coverMedia, imageSize, imageSize);
            activityCoverImage.setTag(R.id.imageViewUri, coverImageURI);
            activityCoverImage.setScaleType(imageScaleType);
            LogUtil.d(SingleFragmentActivity.class.getName(), coverImageURI + " should be loaded into " + activityCoverImage.toString());
            int limitLarge = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("server_download_limit_large", "50")) * 1000;
            activity.getBackgroundTasksHandlerThread().addListener(createTaskListener(coverImageURI));
            activity.getBackgroundTasksHandlerThread().queueGetMediaResource(activityCoverImage, coverImageURI, limitLarge);
            activity.getBackgroundTasksHandlerThread().queueCleanCache();

            findViewById(R.id.asyncImageProgressLayout).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.asyncImageProgressLayout).setVisibility(View.GONE);
        }
    }

    private BackgroundTasksHandlerThread.Listener createTaskListener(final URI requestedURI) {
        return new BackgroundTasksHandlerThread.Listener() {
            @Override
            public BackgroundTasksHandlerThread.ListenerAction onDone(Object[] parameters, Object response, BackgroundTask task) {
                if (task == BackgroundTask.DISPLAY_IMAGE) {
                    boolean isImageViewAndURIReturned = parameters[0] instanceof ImageView && parameters[1] instanceof URI;
                    findViewById(R.id.asyncImageProgressLayout).setVisibility(View.GONE);
                    if (isImageViewAndURIReturned) {
                        ImageView imageView = (ImageView) parameters[0];
                        URI loadedURI = (URI) parameters[1];
                        if (loadedURI.equals(imageView.getTag(R.id.imageViewUri))) {
                            if (response instanceof Bitmap) {
                                Bitmap bitmap = (Bitmap) response;

                                imageView.setImageBitmap(bitmap);

                                imageView.setVisibility(View.VISIBLE);
                            } else {
                                imageView.setVisibility(View.GONE);
                            }

                            if (response instanceof Exception) {
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
                            } else {
                                findViewById(R.id.asyncImageErrorLayout).setVisibility(View.GONE);
                            }
                        } else {
                            LogUtil.d(SingleFragmentActivity.class.getName(), "Image has been loaded but the image view has been recycled and is now used for another image.");
                        }
                        return loadedURI.equals(requestedURI) ? BackgroundTasksHandlerThread.ListenerAction.REMOVE : BackgroundTasksHandlerThread.ListenerAction.KEEP;
                    }
                }
                return BackgroundTasksHandlerThread.ListenerAction.KEEP;
            }
        };
    }

    private void initOverlay(String title) {
        TextView textView = (TextView) findViewById(R.id.asyncImageOverlayLabel);
        if (title != null && title.length() > 0) {
            textView.setText(title);
        } else {
            textView.setVisibility(GONE);
        }
    }

}
