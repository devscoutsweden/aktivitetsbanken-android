package se.devscout.android.view.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.controller.fragment.ActivityBankFragment;

import java.io.Serializable;
public interface WidgetSpecification extends Serializable {
    int getTitleResId();
    boolean isTitleImportant();
    View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, ActivityBankFragment activityBankFragment);
    boolean isDefaultWidget();
}
