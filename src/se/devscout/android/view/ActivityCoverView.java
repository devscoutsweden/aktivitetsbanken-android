package se.devscout.android.view;

import android.content.Context;
import android.graphics.Bitmap;
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
import se.devscout.server.api.model.Media;

import java.net.URI;

public class ActivityCoverView extends FrameLayout implements BackgroundTasksHandlerThread.Listener {

    public ActivityCoverView(Context context) {
        super(context);
        inflate(context);
        init(null, "Activity", (SingleFragmentActivity) context);
    }

    public ActivityCoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context);
        init(null, "Activity", (SingleFragmentActivity) context);
    }

    public ActivityCoverView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context);
        init(null, "Activity", (SingleFragmentActivity) context);
    }

    private void inflate(Context context) {
        SingleFragmentActivity mActivity = (SingleFragmentActivity) context;

        // TODO: Listener is never removed!
        mActivity.getBackgroundTasksHandlerThread().addListener(this);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.activity_cover, this, true);
    }

    public void init(Media coverMedia, String name, SingleFragmentActivity activity) {
        if (coverMedia != null) {
            ImageView activityCoverImage = (ImageView) findViewById(R.id.activityCoverImage);

            int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
            URI coverImageURI = ActivityBankFactory.getInstance(activity).getMediaItemURI(coverMedia, screenWidth, screenWidth/* activityCoverImage.getWidth(), activityCoverImage.getHeight()*/);
            activityCoverImage.setTag(R.id.imageViewUri, coverImageURI);
            LogUtil.d(SingleFragmentActivity.class.getName(), coverImageURI + " should be loaded into " + activityCoverImage.toString());
            activity.getBackgroundTasksHandlerThread().queueGetMediaResource(activityCoverImage, coverImageURI, 50000);
            activity.getBackgroundTasksHandlerThread().queueCleanCache();

            findViewById(R.id.activityCoverProgressBar).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.activityCoverProgressBar).setVisibility(View.GONE);
        }

        final ImageView favIcon = (ImageView) findViewById(R.id.activityCoverFavoriteIcon);
//        favIcon.setColorFilter(container.getResources().getColor(R.color.favoriteTintColor), PorterDuff.Mode.SRC_IN);
        favIcon.setVisibility(View.GONE);

        final ImageView shareIcon = (ImageView) findViewById(R.id.activityCoverShareIcon);
        shareIcon.setVisibility(View.GONE);
/*
        favIcon.setOnClickListener(new View.OnClickListener() {
            private boolean tinted = false;
            @Override
            public void onClick(View view) {
                if (tinted) {
                    favIcon.clearColorFilter();
                } else {
                    favIcon.setColorFilter(container.getResources().getColor(R.color.favoriteTintColor), PorterDuff.Mode.SRC_IN);
                }
                tinted = !tinted;
            }
        });
*/

        TextView textView = (TextView) findViewById(R.id.activityCoverLabel);
        if (name != null && name.length() > 0) {
            textView.setText(name);
        } else {
            textView.setVisibility(GONE);
        }
    }

    @Override
    public void onDone(Object[] parameters, Object response, BackgroundTask task) {
        if (task == BackgroundTask.DISPLAY_IMAGE) {
            if (parameters[0] instanceof ImageView && parameters[1] instanceof URI && response instanceof Bitmap) {
                ImageView imageView = (ImageView) parameters[0];
                URI loadedURI = (URI) parameters[1];
                findViewById(R.id.activityCoverProgressBar).setVisibility(View.GONE);
                if (loadedURI.equals(imageView.getTag(R.id.imageViewUri))) {
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setImageBitmap((Bitmap) response);
                } else {
                    LogUtil.d(SingleFragmentActivity.class.getName(), "Image has been loaded but the image view has been recycled and is now used for another image.");
                }
            }
        }
    }

}
