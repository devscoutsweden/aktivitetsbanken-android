package se.devscout.android.controller.fragment;

import android.support.v4.app.FragmentManager;
import se.devscout.android.R;
import se.devscout.android.view.StaticFragmentsPagerAdapter;

public class StartViewPagerFragment extends ViewPagerFragment {
    @Override
    protected StaticFragmentsPagerAdapter createPagerAdapter(FragmentManager fragmentManager) {
        StaticFragmentsPagerAdapter pagerAdapter = new StaticFragmentsPagerAdapter(fragmentManager);
        pagerAdapter.addTab(R.string.startTabHome, 0, HomeFragment.create());
        pagerAdapter.addTab(R.string.startTabSearch, 0, SearchFragment.create());
        pagerAdapter.addTab(R.string.startTabAgeGroups, 0, AgeGroupListFragment.create());
        pagerAdapter.addTab(R.string.startTabConcepts, 0, CategoryConceptListFragment.create());
        pagerAdapter.addTab(R.string.startTabTracks, 0, CategoryTrackListFragment.create());
        return pagerAdapter;
    }
}
