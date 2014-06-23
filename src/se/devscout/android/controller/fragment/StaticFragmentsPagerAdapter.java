package se.devscout.android.controller.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class StaticFragmentsPagerAdapter extends FragmentPagerAdapter {
    private final Context mContext;
    private List<TabInfo> mTabInfoList = new ArrayList<TabInfo>();
    private List<Fragment> mFragments = new ArrayList<Fragment>();

    public StaticFragmentsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
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
    public CharSequence getPageTitle(int position) {
        return mContext.getString(mTabInfoList.get(position).getNameResId());
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
