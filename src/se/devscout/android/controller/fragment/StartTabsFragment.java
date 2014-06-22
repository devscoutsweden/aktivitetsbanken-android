package se.devscout.android.controller.fragment;

import android.support.v4.app.FragmentManager;
import se.devscout.android.R;
import se.devscout.android.view.StaticFragmentsPagerAdapter;

public class StartTabsFragment extends TabsFragment {
    @Override
    protected StaticFragmentsPagerAdapter createPagerAdapter(FragmentManager fragmentManager) {
        StaticFragmentsPagerAdapter pagerAdapter = new StaticFragmentsPagerAdapter(fragmentManager, getActivity());
        pagerAdapter.addTab(R.string.startTabHome, R.drawable.ic_drawer, StartWidgetFragment.create());
//        pagerAdapter.addTab(R.string.startTabHome , R.drawable.ic_action_favorite, HomeFragment.create());
        pagerAdapter.addTab(R.string.startTabSearch, R.drawable.ic_action_search, SearchFragment.create());
        pagerAdapter.addTab(R.string.startTabCategories, R.drawable.ic_action_labels, CategoriesListFragment.create());
        pagerAdapter.addTab(R.string.startTabFavourites, R.drawable.ic_action_important, FavouriteActivitiesListFragment.create());
//        pagerAdapter.addTab(R.string.startTabAgeGroups, R.drawable.ic_action_cc_bcc, AgeGroupListFragment.create());
//        pagerAdapter.addTab(R.string.startTabTracks, 0, CategoryTrackListFragment.create());
        return pagerAdapter;
    }
}
