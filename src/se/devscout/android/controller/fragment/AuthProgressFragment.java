package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;

public class AuthProgressFragment extends ActivityBankFragment {

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        if (savedInstanceState != null) {
        }

        final View view = inflater.inflate(R.layout.auth_progress, container, false);

        return view;
    }

    public static AuthProgressFragment create() {
        return new AuthProgressFragment();
    }

}
