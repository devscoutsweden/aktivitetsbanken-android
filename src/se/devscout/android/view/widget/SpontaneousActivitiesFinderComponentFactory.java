package se.devscout.android.view.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.view.AbstractActivitiesFinderComponentFactory;
import se.devscout.android.view.FindSpontaneousActivitiesView;

public class SpontaneousActivitiesFinderComponentFactory extends AbstractActivitiesFinderComponentFactory implements WidgetSpecification {
    public SpontaneousActivitiesFinderComponentFactory(int nameResId, int iconResId) {
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
