package se.devscout.android.view.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.view.AbstractActivitiesFinderComponentFactory;
import se.devscout.android.view.LogInView;

public class LogInWidgetSpecification extends AbstractActivitiesFinderComponentFactory {

    public LogInWidgetSpecification(int nameResId, int iconResId) {
        super(iconResId, nameResId, true, false);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, ActivityBankFragment activityBankFragment) {
        return new LogInView(activityBankFragment.getActivity(), true);
    }
}
