package se.devscout.android.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.util.ResourceUtil;
import se.devscout.android.view.ActivitiesListItem;

import java.net.URI;

public class ActivityCoverFragment extends ActivityBankFragment implements View.OnClickListener {

    public static interface OnClickListener {

        void onImageClick(View view, ActivitiesListItem localActivity, Context ctx);
    }

    private ActivitiesListItem mActivity;

    private OnClickListener mOnClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initListItemView(inflater, container, null, mActivity);

        if (mOnClickListener != null) {
            view.setOnClickListener(this);
        }

        return view;
    }

    public static View initListItemView(LayoutInflater inflater, final ViewGroup container, View view, ActivitiesListItem activityRevision) {
        if (view == null) {
            view = inflater.inflate(R.layout.activity_cover, container, false);
        }

        URI coverMedia = activityRevision.getCoverMedia();
        if (coverMedia != null) {
            final ImageView imageView = (ImageView) view.findViewById(R.id.activityCoverImage);
            imageView.setImageResource(new ResourceUtil(container.getContext()).toResourceId(coverMedia));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        final ImageView favIcon = (ImageView) view.findViewById(R.id.activityCoverFavoriteIcon);
//        favIcon.setColorFilter(container.getResources().getColor(R.color.favoriteTintColor), PorterDuff.Mode.SRC_IN);
        favIcon.setVisibility(View.GONE);

        final ImageView shareIcon = (ImageView) view.findViewById(R.id.activityCoverShareIcon);
        shareIcon.setVisibility(View.GONE);
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

    public static ActivityCoverFragment create(ActivitiesListItem properties, OnClickListener onClickListener) {
        ActivityCoverFragment fragment = new ActivityCoverFragment();
        fragment.mActivity = properties;
        fragment.mOnClickListener = onClickListener;
        return fragment;
    }
}
