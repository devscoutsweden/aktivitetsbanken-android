package se.devscout.android.controller.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import se.devscout.android.model.MediaKey;
import se.devscout.android.model.ObjectIdentifierBean;
import se.devscout.android.view.AsyncImageBean;

import java.util.ArrayList;
import java.util.List;

public class GalleryFullsizePagerFragment extends PagerFragment {

    protected FragmentStatePagerAdapter createPagerAdapter() {
        return new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                MediaKey key = mKeys.get(i);
                return AsyncImageFragment.create(new AsyncImageBean(null, getFullScreenMediaURIs(key)), null);
            }

            @Override
            public int getCount() {
                return mKeys.size();
            }
        };
    }

    public static GalleryFullsizePagerFragment create(List<? extends MediaKey> mediaKeys, /*ArrayList<String> titles, */int selectedIndex) {
        GalleryFullsizePagerFragment fragment = new GalleryFullsizePagerFragment();
        ArrayList<ObjectIdentifierBean> keys = new ArrayList<ObjectIdentifierBean>();
        for (MediaKey key : mediaKeys) {
            keys.add(new ObjectIdentifierBean(key.getId()));
        }
        fragment.mKeys = keys;
//        fragment.mTitles = titles;
        fragment.mSelectedIndex = selectedIndex;
        return fragment;
    }

}
