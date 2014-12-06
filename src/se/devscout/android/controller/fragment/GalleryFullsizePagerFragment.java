package se.devscout.android.controller.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import se.devscout.android.model.ActivityBean;
import se.devscout.android.model.ObjectIdentifierBean;
import se.devscout.android.view.ActivitiesListItem;
import se.devscout.server.api.model.MediaKey;

import java.util.ArrayList;
import java.util.List;

public class GalleryFullsizePagerFragment extends PagerFragment {

    protected FragmentStatePagerAdapter createPagerAdapter() {
        return new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                // TODO: Implement new POJO for holding data, rather than relying on this mock object.
                MediaKey key = mKeys.get(i);
                ActivityBean mock = new ActivityBean(null, 0L, 0L, 0L, false);
//                mock.setName(mTitles != null && mTitles.get(i) != null ? mTitles.get(i) : "Image " + (i + 1));
                mock.getMediaItems().add(getActivityBank().readMediaItem(key));
                return ActivityCoverFragment.create(new ActivitiesListItem(mock), null);
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
