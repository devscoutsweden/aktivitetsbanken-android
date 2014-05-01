package se.devscout.android.controller.fragment;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.util.ActivityUtil;
import se.devscout.android.util.ResourceUtil;
import se.devscout.server.api.model.Activity;
import se.devscout.server.api.model.ActivityRevision;
import se.devscout.server.api.model.Media;

public class ActivityCoverFragment extends Fragment implements View.OnClickListener {

    public static interface OnClickListener {

        void onImageClick(View view, Activity localActivity, Context ctx);
    }

    private Activity mActivity;

    private OnClickListener mOnClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initListItemView(inflater, container, null, ActivityUtil.getLatestActivityRevision(mActivity));

        if (mOnClickListener != null) {
            view.setOnClickListener(this);
        }

        return view;
    }

    public static View initListItemView(LayoutInflater inflater, final ViewGroup container, View view, ActivityRevision activityRevision) {
        if (view == null) {
            view = inflater.inflate(R.layout.activity_cover, container, false);
        }

        Media coverMedia = activityRevision.getCoverMedia();
        if (coverMedia != null) {
            final ImageView imageView = (ImageView) view.findViewById(R.id.activityCoverImage);
            imageView.setImageResource(new ResourceUtil(container.getContext()).toResourceId(coverMedia.getURI()));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        final ImageView favIcon = (ImageView) view.findViewById(R.id.activityCoverFavoriteIcon);
        favIcon.setColorFilter(container.getResources().getColor(R.color.favoriteTintColor), PorterDuff.Mode.SRC_IN);
/*
        favIcon.setOnClickListener(new View.OnClickListener() {
            private boolean tinted = false;
            @Override
            public void onClick(View view) {
                if (tinted) {
                    favIcon.clearColorFilter();
                } else {
                    favIcon.setColorFilter(container.getResources().getColor(R.color.favoriteTintColor), PorterDuff.Mode.SRC_IN);
                }
                tinted = !tinted;
            }
        });
*/

        TextView textView = (TextView) view.findViewById(R.id.activityCoverLabel);
        textView.setText(activityRevision.getName());

        return view;
    }


    @Override
    public void onClick(View view) {
        if (mOnClickListener != null) {
            mOnClickListener.onImageClick(view, mActivity, getActivity());
        }
    }

    public static ActivityCoverFragment create(Activity properties, OnClickListener onClickListener) {
        ActivityCoverFragment fragment = new ActivityCoverFragment();
        fragment.mActivity = properties;
        fragment.mOnClickListener = onClickListener;
        return fragment;
    }
}
