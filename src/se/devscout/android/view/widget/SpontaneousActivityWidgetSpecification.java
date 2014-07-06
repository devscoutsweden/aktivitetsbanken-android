package se.devscout.android.view.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.view.FindSpontaneousActivitiesView;

public class SpontaneousActivityWidgetSpecification implements WidgetSpecification {
    @Override
    public int getTitleResId() {
        return 0;
    }

    @Override
    public View[] getViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, ActivityBankFragment activityBankFragment) {
        return new View[]{new FindSpontaneousActivitiesView(container.getContext(), true)};
    }
}
