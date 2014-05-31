package se.devscout.android.controller.fragment;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.view.StaticFragmentsPagerAdapter;
import se.devscout.android.view.ViewPagerIndicator;

public abstract class TabsFragment extends ActivityBankFragment implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

    protected abstract StaticFragmentsPagerAdapter createPagerAdapter(FragmentManager fragmentManager, boolean landscape);

    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_pager, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        ViewPagerIndicator viewPagerIndicator = (ViewPagerIndicator) view.findViewById(R.id.viewPagerIndicator);
        viewPagerIndicator.setVisibility(View.GONE);
        mViewPager.setOnPageChangeListener(this);
        boolean landscape = getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        StaticFragmentsPagerAdapter pageAdapter = createPagerAdapter(getChildFragmentManager(), landscape);
        mViewPager.setAdapter(pageAdapter);

        ActionBar actionBar = getActivity().getActionBar();
        if (pageAdapter.getTabInfoList().size() > 1) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }
        for (StaticFragmentsPagerAdapter.TabInfo tabNameResourceId : pageAdapter.getTabInfoList()) {
            ActionBar.Tab tab = actionBar.newTab().setTabListener(this);
            if (tabNameResourceId.getIconResId() > 0) {
                tab.setIcon(tabNameResourceId.getIconResId());
            }
            if (tabNameResourceId.getNameResId() > 0) {
                tab.setText(tabNameResourceId.getNameResId());
            }
            actionBar.addTab(tab);
        }

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

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        getActivity().getActionBar().setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
