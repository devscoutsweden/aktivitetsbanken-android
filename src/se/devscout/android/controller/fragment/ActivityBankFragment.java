package se.devscout.android.controller.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.server.api.ActivityBank;

public class ActivityBankFragment extends Fragment {
    protected ActivityBank getActivityBank() {
        return getActivityBank(getActivity());
    }

    protected ActivityBank getActivityBank(Context context) {
        return ActivityBankFactory.getInstance(context);
    }

    protected SharedPreferences getPreferences() {
        return getActivity().getPreferences(Context.MODE_PRIVATE);
    }
}
