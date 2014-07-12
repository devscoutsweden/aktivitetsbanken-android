package se.devscout.android.controller.fragment;

import android.support.v4.app.FragmentManager;
import se.devscout.android.util.PreferencesUtil;
import se.devscout.android.view.AbstractActivitiesFinderComponentFactory;

import java.util.List;

public class StartTabsFragment extends TabsFragment {
    @Override
    protected StaticFragmentsPagerAdapter createPagerAdapter(FragmentManager fragmentManager) {
        StaticFragmentsPagerAdapter pagerAdapter = new StaticFragmentsPagerAdapter(fragmentManager, getActivity());


        List<String> widgetIds = PreferencesUtil.getStringList(getPreferences(), "tabs", null);
        for (AbstractActivitiesFinderComponentFactory finder : AbstractActivitiesFinderComponentFactory.getActivitiesFinders()) {
            if (finder.isFragmentCreator()) {
                if ((widgetIds != null && widgetIds.contains(finder.getId())) || (widgetIds == null && finder.isDefaultFragment())) {
                    pagerAdapter.addTab(finder.getTitleResId(), finder.getIconResId(), finder.createFragment());
                }
            }
        }
        return pagerAdapter;
    }
}
