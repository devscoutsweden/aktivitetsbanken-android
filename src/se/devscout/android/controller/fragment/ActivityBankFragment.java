package se.devscout.android.controller.fragment;

import android.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.View;
import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.concurrency.BackgroundTasksHandlerThread;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.model.Media;
import se.devscout.server.api.model.MediaKey;

import java.net.URI;

public class ActivityBankFragment extends Fragment {

    protected ActivityBank getActivityBank() {
        return getActivityBank(getActivity());
    }

    protected ActivityBank getActivityBank(Context context) {
        return ActivityBankFactory.getInstance(context);
    }

    protected SharedPreferences getPreferences() {
        return getActivity().getSharedPreferences(getClass().getName(), Context.MODE_PRIVATE);
    }

    protected BackgroundTasksHandlerThread getBackgroundTasksHandlerThread(View view) {
        return getBackgroundTasksHandlerThread(view.getRootView().getContext());
    }

    protected BackgroundTasksHandlerThread getBackgroundTasksHandlerThread(Context context) {
        return ((SingleFragmentActivity) context).getBackgroundTasksHandlerThread();
    }

    protected URI[] getFullScreenMediaURIs(MediaKey key) {
        Media media = getActivityBank().readMediaItem(key);
        return getFullScreenMediaURIs(media);
    }

    protected URI[] getFullScreenMediaURIs(Media media) {
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int[] widths = new int[]{
                Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels),
                Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels),
                getActivity().getResources().getDimensionPixelSize(R.dimen.thumbnail_width)
        };
        URI[] uris = new URI[widths.length];
        for (int i = 0; i < widths.length; i++) {
            int width = widths[i];
            uris[i] = getActivityBank().getMediaItemURI(media, width, width);
        }
        return uris;
    }
}
