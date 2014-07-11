package se.devscout.android.view.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.view.AbstractActivitiesFinder;
import se.devscout.android.view.FindSpontaneousActivitiesView;

public class SpontaneousActivitiesFinder extends AbstractActivitiesFinder implements WidgetSpecification {
    public SpontaneousActivitiesFinder(int nameResId, int iconResId) {
        super(iconResId, nameResId);
    }

    @Override
    public boolean isTitleImportant() {
        return false;
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, ActivityBankFragment activityBankFragment) {
        return new FindSpontaneousActivitiesView(container.getContext(), true);
    }

    @Override
    public boolean isDefaultWidget() {
        return true;
    }
}
