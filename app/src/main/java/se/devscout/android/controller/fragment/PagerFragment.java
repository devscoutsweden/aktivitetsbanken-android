package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import se.devscout.android.R;
import se.devscout.android.model.ObjectIdentifierBean;
import se.devscout.android.util.LogUtil;
import se.devscout.android.view.CustomViewPagerIndicator;

public abstract class PagerFragment extends ActivityBankFragment implements ViewPager.OnPageChangeListener {
    protected ArrayList<ObjectIdentifierBean> mKeys;
    protected ArrayList<String> mTitles;
    protected int mSelectedIndex;
    protected CustomViewPagerIndicator mCustomViewPagerIndicator;
    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            /*
             * Restore fields from saved state, for example after the device has been rotated.
             */
            mKeys = (ArrayList<ObjectIdentifierBean>) savedInstanceState.getSerializable("mKeys");
            mTitles = (ArrayList<String>) savedInstanceState.getSerializable("mTitles");
            mSelectedIndex = savedInstanceState.getInt("mSelectedIndex");
        }

        setHasOptionsMenu(true);

        final View view = inflater.inflate(getViewPagerLayout(), container, false);
        TabLayout mSlidingTabLayout = (TabLayout) view.findViewById(R.id.viewPagerSlidingTabs);
        mSlidingTabLayout.setVisibility(View.GONE);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mCustomViewPagerIndicator = (CustomViewPagerIndicator) view.findViewById(R.id.viewPagerIndicator);
        mCustomViewPagerIndicator.setVisibility(mKeys.size() > 1 ? View.VISIBLE : View.GONE);
        mCustomViewPagerIndicator.setCount(mKeys.size());
        mViewPager.setOnPageChangeListener(this);
        FragmentStatePagerAdapter pageAdapter = createPagerAdapter();
        mViewPager.setAdapter(pageAdapter);
        mViewPager.setCurrentItem(mSelectedIndex);
        updateActivityTitle(mSelectedIndex);

        return view;
    }

    protected int getViewPagerLayout() {
        return R.layout.view_pager;
    }

    protected abstract FragmentStatePagerAdapter createPagerAdapter();

    protected void updateActivityTitle(int selectedIndex) {
        getActivity().setTitle(mTitles != null && mTitles.get(selectedIndex) != null ? mTitles.get(selectedIndex) : "Image " + (selectedIndex + 1));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
         * Store fields into saved state, for example when the activity is destroyed after the device has been rotated.
         */
        LogUtil.d(PagerFragment.class.getName(), "Saving state");
        outState.putSerializable("mKeys", mKeys);
        outState.putSerializable("mTitles", mTitles);
        outState.putInt("mSelectedIndex", mSelectedIndex);
        LogUtil.d(PagerFragment.class.getName(), "State saved");
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        updateActivityTitle(i);
        mSelectedIndex = i;
        mCustomViewPagerIndicator.setSelectedIndex(mSelectedIndex);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }
}
