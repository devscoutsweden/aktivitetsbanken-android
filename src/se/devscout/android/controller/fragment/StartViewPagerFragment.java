package se.devscout.android.controller.fragment;

import android.support.v4.app.FragmentManager;
import se.devscout.android.R;
import se.devscout.android.view.StaticFragmentsPagerAdapter;

public class StartViewPagerFragment extends ViewPagerFragment {
    @Override
    protected StaticFragmentsPagerAdapter createPagerAdapter(FragmentManager fragmentManager) {
        StaticFragmentsPagerAdapter pagerAdapter = new StaticFragmentsPagerAdapter(fragmentManager);
        pagerAdapter.addTab(R.string.startTabHome, HomeFragment.create());
        pagerAdapter.addTab(R.string.startTabAgeGroups, AgeGroupListFragment.create());
        pagerAdapter.addTab(R.string.startTabConcepts, CategoryConceptListFragment.create());
        pagerAdapter.addTab(R.string.startTabTracks, CategoryTrackListFragment.create());
        return pagerAdapter;
    }
}
