package se.devscout.android.view.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.view.FindSpontaneousActivitiesView;

public class SpontaneousActivitiesFinderComponentFactory extends AbstractActivitiesFinderComponentFactory implements WidgetComponentFactory {
    public SpontaneousActivitiesFinderComponentFactory(String name) {
        super(name, R.string.startTabSpontaneousActivity, R.drawable.ic_drawer, false);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, ActivityBankFragment activityBankFragment) {
        return new FindSpontaneousActivitiesView(container.getContext(), true);
    }
}
