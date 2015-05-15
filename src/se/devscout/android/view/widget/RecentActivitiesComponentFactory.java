package se.devscout.android.view.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.view.RecentActivitiesListView;

public class RecentActivitiesComponentFactory extends AbstractComponentFactory implements WidgetComponentFactory {
    public RecentActivitiesComponentFactory(String name) {
        super(name, R.string.startTabRecentActivities, R.drawable.ic_action_important, true);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, ActivityBankFragment activityBankFragment) {

        RecentActivitiesListView mView = new RecentActivitiesListView(container.getContext(), 0, 0, true);

        mView.runSearchTaskInNewThread();

        return mView;
    }

}
