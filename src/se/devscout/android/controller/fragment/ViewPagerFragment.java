package se.devscout.android.controller.fragment;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.view.StaticFragmentsPagerAdapter;

public abstract class ViewPagerFragment extends Fragment implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

    protected abstract StaticFragmentsPagerAdapter createPagerAdapter(FragmentManager fragmentManager);

    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewPager = (ViewPager) inflater.inflate(R.layout.view_pager, container, false);
        mViewPager.setOnPageChangeListener(this);
        StaticFragmentsPagerAdapter pageAdapter = createPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(pageAdapter);

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        for (int tabNameResourceId : pageAdapter.getTabNameResourceIds()) {
            actionBar.addTab(actionBar.newTab().setText(tabNameResourceId).setTabListener(this));
        }

        return mViewPager;
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
