package se.devscout.android.controller.fragment;

import android.support.v4.app.FragmentManager;
import se.devscout.android.R;
import se.devscout.android.view.StaticFragmentsPagerAdapter;

public class StartViewPagerFragment extends ViewPagerFragment {
    @Override
    protected StaticFragmentsPagerAdapter createPagerAdapter(FragmentManager fragmentManager, boolean landscape) {
        StaticFragmentsPagerAdapter pagerAdapter = new StaticFragmentsPagerAdapter(fragmentManager);
        pagerAdapter.addTab(landscape ? R.string.startTabHome : 0, R.drawable.ic_action_favorite, HomeFragment.create());
        pagerAdapter.addTab(landscape ? R.string.startTabSearch : 0, R.drawable.ic_action_search, SearchFragment.create());
        pagerAdapter.addTab(landscape ? R.string.startTabAgeGroups : 0, R.drawable.ic_action_cc_bcc, AgeGroupListFragment.create());
        pagerAdapter.addTab(landscape ? R.string.startTabConcepts : 0, R.drawable.ic_action_labels, CategoriesListFragment.create());
//        pagerAdapter.addTab(R.string.startTabTracks, 0, CategoryTrackListFragment.create());
        return pagerAdapter;
    }
}
