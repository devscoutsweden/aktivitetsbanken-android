package se.devscout.android.view.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

public interface StartScreenWidget extends Serializable {
    int getTitleResId();
    View[] getViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    void onFragmentResume();
}
