package se.devscout.android.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.controller.fragment.ActivityBankFragment;

public interface WidgetComponentFactory {
    boolean isWidgetTitleImportant();

    String getId();

    int getIconResId();

    int getTitleResId();

    View createView(LayoutInflater inflater, ViewGroup container, ActivityBankFragment activityBankFragment);
}
