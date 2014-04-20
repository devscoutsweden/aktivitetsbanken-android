package se.devscout.android.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class StaticFragmentsPagerAdapter extends FragmentPagerAdapter {
    private List<Integer> mTabNameResourceIds = new ArrayList<Integer>();
    private List<Fragment> mFragments = new ArrayList<Fragment>();

    public StaticFragmentsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addTab(int tabNameResId, Fragment fragment) {
        mTabNameResourceIds.add(tabNameResId);
        mFragments.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    public List<Integer> getTabNameResourceIds() {
        return mTabNameResourceIds;
    }
}
