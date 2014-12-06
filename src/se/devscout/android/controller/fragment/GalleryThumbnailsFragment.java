package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import se.devscout.android.R;
import se.devscout.android.controller.activity.GalleryFullscreenActivity;
import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.android.model.ObjectIdentifierBean;
import se.devscout.android.util.LogUtil;
import se.devscout.android.view.ActivityCoverView;
import se.devscout.server.api.model.Media;
import se.devscout.server.api.model.MediaKey;

import java.util.ArrayList;
import java.util.List;

public class GalleryThumbnailsFragment extends ActivityBankFragment {
    private GridView mGridView;

    protected ArrayList<ObjectIdentifierBean> mActivityKeys;
    private int mSize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            /*
             * Restore fields from saved state, for example after the device has been rotated.
             */
            mActivityKeys = (ArrayList<ObjectIdentifierBean>) savedInstanceState.getSerializable("mActivityKeys");
            mSize = savedInstanceState.getInt("mSize");
        } else {
            mSize = container.getContext().getResources().getDimensionPixelSize(android.R.dimen.thumbnail_height);
        }
        View view = inflater.inflate(R.layout.gallery_thumbnails, container, false);

        mGridView = (GridView) view.findViewById(R.id.galleryThumbnailsGrid);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getActivity().startActivity(GalleryFullscreenActivity.createIntent(getActivity(), mActivityKeys, null, i));
            }
        });

        initGridView();

        return view;
    }

    private void initGridView() {
        if (getActivity() == null || mGridView == null) {
            return;
        }
        ArrayAdapter<ObjectIdentifierBean> adapter = new ArrayAdapter<ObjectIdentifierBean>(getActivity(), android.R.layout.simple_gallery_item, mActivityKeys) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = new ActivityCoverView(parent.getContext());
                }
                ActivityCoverView view = (ActivityCoverView) convertView;

                Media media = getActivityBank().readMediaItem(getItem(position));
                view.init(media, null, (SingleFragmentActivity) getActivity(), mSize, ImageView.ScaleType.CENTER_CROP);
                view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mSize));

                return view;
            }
        };
        mGridView.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
         * Store fields into saved state, for example when the activity is destroyed after the device has been rotated.
         */
        LogUtil.d(ActivitiesListFragment.class.getName(), "Saving state");
        outState.putSerializable("mActivityKeys", mActivityKeys);
        outState.putInt("mSize", mSize);
        LogUtil.d(ActivitiesListFragment.class.getName(), "State saved");
    }

    public static GalleryThumbnailsFragment create(List<? extends MediaKey> activities) {
        GalleryThumbnailsFragment fragment = new GalleryThumbnailsFragment();
        ArrayList<ObjectIdentifierBean> activityKeys = new ArrayList<ObjectIdentifierBean>();
        for (MediaKey key : activities) {
            activityKeys.add(new ObjectIdentifierBean(key.getId()));
        }
        fragment.mActivityKeys = activityKeys;
        return fragment;
    }
}
