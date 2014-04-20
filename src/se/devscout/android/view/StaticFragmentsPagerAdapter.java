package se.devscout.android.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class StaticFragmentsPagerAdapter extends FragmentPagerAdapter {
    private List<TabInfo> mTabInfoList = new ArrayList<TabInfo>();
    private List<Fragment> mFragments = new ArrayList<Fragment>();

    public StaticFragmentsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addTab(int tabNameResId, int iconResId, Fragment fragment) {
        mTabInfoList.add(new TabInfo(iconResId, tabNameResId));
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

    public List<TabInfo> getTabInfoList() {
        return mTabInfoList;
    }

    public static class TabInfo {
        private int mNameResId;
        private int mIconResId;

        public TabInfo(int iconResId, int nameResId) {
            mIconResId = iconResId;
            mNameResId = nameResId;
        }

        public int getIconResId() {
            return mIconResId;
        }

        public int getNameResId() {
            return mNameResId;
        }
    }
}
