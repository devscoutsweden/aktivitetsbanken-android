package se.devscout.android.controller.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import se.devscout.android.R;

public class WelcomeMessageWidget implements StartScreenWidget {

    public WelcomeMessageWidget() {
    }

    @Override
    public int getTitleResId() {
        return R.string.startTabHome;
    }

    @Override
    public View[] getViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView textView = new TextView(container.getContext());
        textView.setText(R.string.startIntroText);
        return new View[]{textView};
    }

    @Override
    public void onFragmentResume() {
    }
}
