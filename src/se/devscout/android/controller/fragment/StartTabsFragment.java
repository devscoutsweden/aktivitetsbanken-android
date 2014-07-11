package se.devscout.android.controller.fragment;

import android.support.v4.app.FragmentManager;
import se.devscout.android.controller.activity.FragmentCreator;
import se.devscout.android.util.PreferencesUtil;
import se.devscout.android.view.AbstractActivitiesFinder;

import java.util.List;

public class StartTabsFragment extends TabsFragment {
    @Override
    protected StaticFragmentsPagerAdapter createPagerAdapter(FragmentManager fragmentManager) {
        StaticFragmentsPagerAdapter pagerAdapter = new StaticFragmentsPagerAdapter(fragmentManager, getActivity());


        List<String> widgetIds = PreferencesUtil.getStringList(getPreferences(), "tabs", null);
        for (AbstractActivitiesFinder finder : AbstractActivitiesFinder.getActivitiesFinders()) {
            if (finder.isStartTabCreator()) {
                FragmentCreator widgetSpec = finder.asFragmentCreator();
                if ((widgetIds != null && widgetIds.contains(finder.getId())) || widgetSpec.isDefaultTab()) {
                    pagerAdapter.addTab(finder.getTitleResId(), finder.getIconResId(), finder.asFragmentCreator().createFragment());
                }
            }
        }
        return pagerAdapter;
    }
}
