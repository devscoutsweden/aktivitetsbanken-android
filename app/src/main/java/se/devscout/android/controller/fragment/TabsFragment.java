package se.devscout.android.controller.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.devscout.android.R;
import se.devscout.android.view.CustomViewPagerIndicator;

public abstract class TabsFragment extends ActivityBankFragment {

    private TabLayout mSlidingTabLayout;

    protected abstract StaticFragmentsPagerAdapter createPagerAdapter(FragmentManager fragmentManager);

    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_pager, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        CustomViewPagerIndicator customViewPagerIndicator = (CustomViewPagerIndicator) view.findViewById(R.id.viewPagerIndicator);
        customViewPagerIndicator.setVisibility(View.GONE);
        StaticFragmentsPagerAdapter pageAdapter = createPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(pageAdapter);

        mSlidingTabLayout = (TabLayout) view.findViewById(R.id.viewPagerSlidingTabs);
        mSlidingTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mSlidingTabLayout.setupWithViewPager(mViewPager);

        mViewPager.setCurrentItem(Math.min(getPreferences().getInt(getClass().getSimpleName() + "-selectedViewPageIndex", 0), pageAdapter.getCount() - 1));

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = getPreferences().edit();
        int currentItem = ((ViewPager) getView().findViewById(R.id.viewPager)).getCurrentItem();
        editor.putInt(getClass().getSimpleName() + "-selectedViewPageIndex", currentItem);
        editor.commit();
    }
}
