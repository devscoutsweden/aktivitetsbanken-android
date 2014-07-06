package se.devscout.android.controller.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.view.widget.FragmentListener;
import se.devscout.server.api.ActivityBank;

import java.util.ArrayList;
import java.util.List;

public class ActivityBankFragment extends Fragment {

    //TODO: Useless. Remove!?
    private List<FragmentListener> mListeners = new ArrayList<FragmentListener>();

    protected ActivityBank getActivityBank() {
        return getActivityBank(getActivity());
    }

    protected ActivityBank getActivityBank(Context context) {
        return ActivityBankFactory.getInstance(context);
    }

    protected SharedPreferences getPreferences() {
        return getActivity().getPreferences(Context.MODE_PRIVATE);
    }

    //TODO: Useless. Remove!?
    public void addListener(FragmentListener listener) {
        mListeners.add(listener);
    }

    //TODO: Useless. Remove!?
    public void removeListener(FragmentListener listener) {
        mListeners.remove(listener);
    }

    //TODO: Useless. Remove!?
    protected void fireFragmentResumeEvent() {
        for (FragmentListener listener : mListeners) {
//            listener.onFragmentResume(mRefreshResultOnResume);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fireFragmentResumeEvent();
    }
}
