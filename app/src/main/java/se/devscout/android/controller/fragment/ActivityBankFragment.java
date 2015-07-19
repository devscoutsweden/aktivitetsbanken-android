package se.devscout.android.controller.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.view.View;
import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.android.model.Media;
import se.devscout.android.model.MediaKey;
import se.devscout.android.model.repo.ActivityBank;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.ResourceUtil;
import se.devscout.android.util.concurrency.BackgroundTasksHandlerThread;

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
        return ResourceUtil.getFullScreenMediaURIs(media, getActivity());
    }
}
