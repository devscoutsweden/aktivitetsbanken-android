package se.devscout.android.view.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.view.LogInView;

public class LogInWidgetSpecification extends AbstractActivitiesFinderComponentFactory implements WidgetComponentFactory {

    public LogInWidgetSpecification(String name) {
        super(name, R.string.auth_widget_title, R.drawable.ic_drawer, false);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, ActivityBankFragment activityBankFragment) {
        return new LogInView(activityBankFragment.getActivity(), true);
    }
}
